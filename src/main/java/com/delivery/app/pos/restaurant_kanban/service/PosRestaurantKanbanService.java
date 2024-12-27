package com.delivery.app.pos.restaurant_kanban.service;

import com.delicias.kafka.core.dto.KafkaTopicOrderDTO;
import com.delicias.kafka.core.enums.TOPIC_ORDER_ACTION;
import com.delivery.app.configs.DeliciasAppProperties;
import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.kafka.producer.KafkaTopicOrderProducer;
import com.delivery.app.pos.enums.KanbanStatus;
import com.delivery.app.pos.enums.OrderStatus;
import com.delivery.app.pos.order.models.PosOrder;
import com.delivery.app.pos.order.repositories.PosOrderRepository;
import com.delivery.app.pos.restaurant_kanban.dtos.PosKanbanOrderDTO;
import com.delivery.app.pos.restaurant_kanban.dtos.PosRestaurantKanbanDTO;
import com.delivery.app.pos.restaurant_kanban.dtos.UpdatePosRestaurantKanbanDTO;
import com.delivery.app.pos.order.exception.InvalidProcessStatusOrder;
import com.delivery.app.pos.restaurant_kanban.model.PosRestaurantKanban;
import com.delivery.app.pos.restaurant_kanban.repository.PosRestaurantKanbanRepository;
import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import com.delivery.app.restaurant.template.repository.RestaurantTemplateRepository;
import com.delivery.app.security.dtos.UserDTO;
import com.delivery.app.security.services.KeycloakUserService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PosRestaurantKanbanService {

    private final PosRestaurantKanbanRepository posRestaurantKanbanRepository;
    private final KafkaTopicOrderProducer kafkaTopicOrderProducer;
    private final RestaurantTemplateRepository restaurantTemplateRepository;
    private final PosOrderRepository posOrderRepository;
    private final KeycloakUserService keycloakUserService;
    private final DeliciasAppProperties deliciasAppProperties;


    @Transactional(readOnly = true)
    public PosRestaurantKanbanDTO loadKanban(Integer restaurantId) {

        RestaurantTemplate restaurantTemplate = restaurantTemplateRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantTmpl", "id", restaurantId));

        Map<KanbanStatus, List<PosRestaurantKanbanDTO.Order>> kanbanMap = posRestaurantKanbanRepository.findByRestaurantTmplId(restaurantId)
                .stream()
                .map(i -> PosRestaurantKanbanDTO.Order.builder()
                        .kanbanId(i.getId())
                        .id(i.getOrder().getId())
                        .status(i.getStatus())
                        .amountTotal(i.getOrder().getAmountTotal())
                        .dateOrdered(i.getOrder().getCreatedAt())
                        .hasNote(Optional.ofNullable(i.getOrder().getNotes()).map(r-> !r.trim().isEmpty()).orElse(false))
                        .products(i.getOrder().getLines().stream().map(p ->
                                PosRestaurantKanbanDTO.Product.builder()
                                        .name(p.getProductTemplate().getName())
                                        .qty(p.getQuantity())
                                        .attrValuesDesc(p.getAttributeValues().stream()
                                                .map(r->r.getAttributeValue().getName())
                                                .collect(Collectors.joining(", ")))
                                        .build()).toList())
                        .build())
                .collect(Collectors.groupingBy(PosRestaurantKanbanDTO.Order::status));

        List<PosRestaurantKanbanDTO.KanbanList> list =  List.of(
                PosRestaurantKanbanDTO.KanbanList.builder()
                        .id(KanbanStatus.RECEIVED.name())
                        .children(Optional.ofNullable(kanbanMap.get(KanbanStatus.RECEIVED)).orElse(List.of()))
                        .build(),
                PosRestaurantKanbanDTO.KanbanList.builder()
                        .id(KanbanStatus.ACCEPTED.name())
                        .children(Optional.ofNullable(kanbanMap.get(KanbanStatus.ACCEPTED)).orElse(List.of()))
                        .build(),
                PosRestaurantKanbanDTO.KanbanList.builder()
                        .id(KanbanStatus.COOKING.name())
                        .children(Optional.ofNullable(kanbanMap.get(KanbanStatus.COOKING)).orElse(List.of()))
                        .build(),
                PosRestaurantKanbanDTO.KanbanList.builder()
                        .id(KanbanStatus.READY_TO_DELIVER.name())
                        .children(Optional.ofNullable(kanbanMap.get(KanbanStatus.READY_TO_DELIVER)).orElse(List.of()))
                        .build()
        );

        return  PosRestaurantKanbanDTO.builder()
                .label(restaurantTemplate.getName())
                .children(list)
                .build();
    }


    @Transactional
    public void updateStatusKanbanItem(UpdatePosRestaurantKanbanDTO updatePosRestaurantKanbanDTO) {

        PosRestaurantKanban restaurantKanban = posRestaurantKanbanRepository.findById(updatePosRestaurantKanbanDTO.id())
                .orElseThrow(() -> new ResourceNotFoundException("kanban", "id", updatePosRestaurantKanbanDTO.id()));

        Integer orderId = restaurantKanban.getOrder().getId();

        restaurantKanban.setStatus(updatePosRestaurantKanbanDTO.status());

        switch (updatePosRestaurantKanbanDTO.status()) {
            case ACCEPTED -> OrderAccepted(restaurantKanban.getOrder().getId());
            case COOKING -> OrderCooking(restaurantKanban.getOrder().getId());
            case READY_TO_DELIVER -> {

                OrderReadyToDeliver(restaurantKanban.getOrder().getId());

                sendMessageTopicOrderForSearchDelivery(
                        orderId,
                        restaurantKanban.getRestaurantTmpl()
                );
            }
            case DELIVERED_TO_DELIVER -> { // when deliverer receive order

                OrderStatus orderStatus = restaurantKanban.getOrder().getStatus();

                if(orderStatus.equals(OrderStatus.DELIVERY_ROAD_TO_STORE)) {
                    OrderDeliveryRoadToDestination(restaurantKanban.getOrder().getId());
                }
                else {
                    throw new InvalidProcessStatusOrder(orderId, orderStatus.name(), OrderStatus.DELIVERY_ROAD_TO_DESTINATION.name());
                }

            }
            case CANCELLED -> OrderCancelled(restaurantKanban.getOrder().getId());
        }


    }



    public PosRestaurantKanbanDTO.Order findKanban(Integer id) {

        return posRestaurantKanbanRepository.findById(id).map(i ->  PosRestaurantKanbanDTO.Order.builder()
                .kanbanId(i.getId())
                .id(i.getOrder().getId())
                .status(i.getStatus())
                .amountTotal(i.getOrder().getAmountTotal())
                .dateOrdered(i.getOrder().getCreatedAt())
                .hasNote(Optional.ofNullable(i.getOrder().getNotes()).map(r-> !r.trim().isEmpty()).orElse(false))
                .products(i.getOrder().getLines().stream().map(p ->
                        PosRestaurantKanbanDTO.Product.builder()
                                .name(p.getProductTemplate().getName())
                                .qty(p.getQuantity())
                                .attrValuesDesc(p.getAttributeValues().stream()
                                        .map(r->r.getAttributeValue().getName())
                                        .collect(Collectors.joining(", ")))
                                .build()).toList())
                .build()
        ).orElseThrow(
                () -> new ResourceNotFoundException("load kanban", "id", id)
        );
    }

    public PosKanbanOrderDTO findOrderByKanbanId(Integer kanbanId) {

        PosRestaurantKanban kanban = posRestaurantKanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new ResourceNotFoundException("kanban", "id", kanbanId));


        UserDTO deliverer = null;

        if(kanban.getOrder().getDeliveryUserUID() != null) {
            deliverer = keycloakUserService.findById(kanban.getOrder().getDeliveryUserUID().toString());
        }



        return PosKanbanOrderDTO.builder()
                .kanbanId(kanban.getId())
                .order(PosKanbanOrderDTO.Order.builder()
                        .orderId(kanban.getOrder().getId())
                        .hasNote(Optional.ofNullable(kanban.getOrder().getNotes())
                                .map(r-> !r.trim().isEmpty())
                                .orElse(false))
                        .notes(kanban.getOrder().getNotes())
                        .dateOrdered(kanban.getOrder().getCreatedAt())
                        .amountTotal(kanban.getOrder().getAmountTotal())
                        .status(kanban.getOrder().getStatus())
                        .deliverer(Optional.ofNullable(deliverer)
                                .map(d -> PosKanbanOrderDTO.Order.OrderDeliverer.builder()
                                        .name(d.firstName())
                                        .picture(deliciasAppProperties.getFiles().getStaticDefault())
                                .build())
                                .orElse(null))
                        .lines(kanban.getOrder().getLines().stream()
                                .map(l -> PosKanbanOrderDTO.OrderLine.builder()
                                        .qty(l.getQuantity())
                                        .name(l.getProductTemplate().getName())
                                        .picture(
                                                Optional.ofNullable(l.getProductTemplate().getPicture())
                                                        .map(picture -> String.format("%s/%s", deliciasAppProperties.getFiles().getResources(), picture))
                                                        .orElse(deliciasAppProperties.getFiles().getStaticDefault()
                                                )
                                        )
                                        .attrValues(l.getAttributeValues().stream().map(
                                                av -> PosKanbanOrderDTO.OrderLine.AttrValue.builder()
                                                        .attrName(av.getAttributeValue().getAttribute().getName())
                                                        .attrValueName(av.getAttributeValue().getName())
                                                        .build()).toList())
                                                .build())
                                        .toList())
                                .build())
                .build();
    }

    private void OrderAccepted(Integer orderId) {

        PosOrder order = posOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("order", "id", orderId));

        order.setStatus(OrderStatus.ACCEPTED);

        posOrderRepository.save(order);

        sendMessageKafkaTopicOrder(orderId, OrderStatus.ACCEPTED.name());
    }

    private void OrderCooking(Integer orderId) {

        PosOrder order = posOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("order", "id", orderId));

        order.setStatus(OrderStatus.COOKING);

        posOrderRepository.save(order);
        sendMessageKafkaTopicOrder(orderId, OrderStatus.COOKING.name());
    }

    private void OrderReadyToDeliver(Integer orderId) {

        PosOrder order = posOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("order", "id", orderId));

        order.setStatus(OrderStatus.READY);

        posOrderRepository.save(order);
        sendMessageKafkaTopicOrder(orderId, OrderStatus.READY.name());

    }

    private void OrderDeliveryRoadToDestination(Integer orderId) {

        PosOrder order = posOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("order", "id", orderId));


        order.setStatus(OrderStatus.DELIVERY_ROAD_TO_DESTINATION);

        posOrderRepository.save(order);
        sendMessageKafkaTopicOrder(orderId, OrderStatus.DELIVERY_ROAD_TO_DESTINATION.name());
    }

    private void OrderCancelled(Integer orderId) {
        //TODO Falta implementar
    }


    private void sendMessageKafkaTopicOrder(Integer orderId, String status) {

        KafkaTopicOrderDTO.Order order = new KafkaTopicOrderDTO.Order(
                orderId,
                status
        );

        KafkaTopicOrderDTO kafkaTopicOrderDTO = new KafkaTopicOrderDTO();
        kafkaTopicOrderDTO.setAction(TOPIC_ORDER_ACTION.UPDATE_STATUS_ORDER);
        kafkaTopicOrderDTO.setOrder(order);

        kafkaTopicOrderProducer.sendMessageTopicOrder(kafkaTopicOrderDTO);
    }

    @Async
    void sendMessageTopicOrderForSearchDelivery(
            Integer orderId,
            RestaurantTemplate restaurantTmpl
    ) {

        KafkaTopicOrderDTO.OrderRestaurant restaurant = new KafkaTopicOrderDTO.OrderRestaurant(
                new KafkaTopicOrderDTO.GpsPoint(
                        restaurantTmpl.getPosition().getCoordinate().getY(),
                        restaurantTmpl.getPosition().getCoordinate().getX())
        );

        KafkaTopicOrderDTO.Order order = new KafkaTopicOrderDTO.Order(
                orderId,
                restaurant
        );

        KafkaTopicOrderDTO kafkaTopicOrderDTO = new KafkaTopicOrderDTO();
        kafkaTopicOrderDTO.setAction(TOPIC_ORDER_ACTION.SEARCH_DELIVERY);
        kafkaTopicOrderDTO.setOrder(order);

        kafkaTopicOrderProducer.sendMessageTopicOrder(kafkaTopicOrderDTO);
    }
}
