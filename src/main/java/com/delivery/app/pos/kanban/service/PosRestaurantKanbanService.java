package com.delivery.app.pos.kanban.service;

import com.delicias.kafka.core.dto.KafkaTopicOrderDTO;
import com.delicias.kafka.core.enums.TOPIC_ORDER_ACTION;
import com.delivery.app.configs.DeliciasAppProperties;
import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.kafka.producer.KafkaTopicOrderProducer;
import com.delivery.app.pos.enums.OrderStatus;
import com.delivery.app.pos.kanban.dtos.PosKanbanOrderDTO;
import com.delivery.app.pos.kanban.dtos.PosRestaurantKanbanDTO;
import com.delivery.app.pos.kanban.dtos.UpdatePosRestaurantKanbanDTO;
import com.delivery.app.pos.kanban.model.PosRestaurantKanban;
import com.delivery.app.pos.kanban.repository.PosRestaurantKanbanRepository;
import com.delivery.app.pos.order.models.PosOrder;
import com.delivery.app.pos.order.repositories.PosOrderRepository;
import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import com.delivery.app.restaurant.template.repository.RestaurantTemplateRepository;
import com.delivery.app.security.dtos.UserDTO;
import com.delivery.app.security.services.KeycloakUserService;
import com.delivery.app.supabase.order.dtos.SupOrderChangeStatusReqDTO;
import com.delivery.app.supabase.order.service.SupOrderService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final SupOrderService supOrderService;



    @Transactional
    public void updateStatusKanbanItem(UpdatePosRestaurantKanbanDTO kanban) {

        PosOrder order = posOrderRepository.findById(kanban.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("order", "id", kanban.orderId()));

        switch (kanban.status()) {
            case ACCEPTED -> acceptOrder(order);
            case COOKING -> cookingOrder(order);
            case READY_FOR_DELIVERY -> orderReadyToDeliver(order);
        }
    }

    private void acceptOrder(PosOrder order) {

        order.setStatus(OrderStatus.ACCEPTED);
        posOrderRepository.save(order);

        supOrderService.changeStatus(SupOrderChangeStatusReqDTO.builder()
                .orderId(order.getId())
                .status(OrderStatus.ACCEPTED.name())
                .build());
    }

    private void cookingOrder(PosOrder order) {

        order.setStatus(OrderStatus.COOKING);
        posOrderRepository.save(order);

        supOrderService.changeStatus(SupOrderChangeStatusReqDTO.builder()
                .orderId(order.getId())
                .status(OrderStatus.COOKING.name())
                .build());

    }

    private void orderReadyToDeliver(PosOrder order) {

        order.setStatus(OrderStatus.READY_FOR_DELIVERY);
        posOrderRepository.save(order);

        supOrderService.changeStatus(SupOrderChangeStatusReqDTO.builder()
                .orderId(order.getId())
                .status(OrderStatus.READY_FOR_DELIVERY.name())
                .build());
    }


    public PosRestaurantKanbanDTO.Order findKanban(Integer id) {

        return posRestaurantKanbanRepository.findById(id).map(i ->  PosRestaurantKanbanDTO.Order.builder()
                .kanbanId(i.getId())
                .id(i.getOrder().getId())
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
                                        .picture(deliciasAppProperties.getSupabase().getLogo())
                                .build())
                                .orElse(null))
                        .lines(kanban.getOrder().getLines().stream()
                                .map(l -> PosKanbanOrderDTO.OrderLine.builder()
                                        .qty(l.getQuantity())
                                        .name(l.getProductTemplate().getName())
                                        .picture(
                                                Optional.ofNullable(l.getProductTemplate().getPicture())
                                                        .orElse(deliciasAppProperties.getSupabase().getLogo()
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
