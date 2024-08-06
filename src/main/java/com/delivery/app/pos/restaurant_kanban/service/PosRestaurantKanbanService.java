package com.delivery.app.pos.restaurant_kanban.service;

import com.delicias.kafka.core.dto.KafkaTopicOrderDTO;
import com.delicias.kafka.core.enums.TOPIC_ORDER_ACTION;
import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.kafka.producer.KafkaTopicOrderProducer;
import com.delivery.app.pos.enums.KanbanStatus;
import com.delivery.app.pos.restaurant_kanban.dtos.PosRestaurantKanbanDTO;
import com.delivery.app.pos.restaurant_kanban.dtos.UpdatePosRestaurantKanbanDTO;
import com.delivery.app.pos.restaurant_kanban.model.PosRestaurantKanban;
import com.delivery.app.pos.restaurant_kanban.repository.PosRestaurantKanbanRepository;
import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import com.delivery.app.restaurant.template.repository.RestaurantTemplateRepository;
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
    public void updateKanbanItem(UpdatePosRestaurantKanbanDTO updatePosRestaurantKanbanDTO) {

        PosRestaurantKanban restaurantKanban = posRestaurantKanbanRepository.findById(updatePosRestaurantKanbanDTO.id())
                .orElseThrow();

        Integer orderId = restaurantKanban.getOrder().getId();

        restaurantKanban.setStatus(updatePosRestaurantKanbanDTO.status());

        if(updatePosRestaurantKanbanDTO.status().equals(KanbanStatus.READY_TO_DELIVER)) {

            double longitude = restaurantKanban.getRestaurantTmpl().getPosition().getCoordinate().getX();
            double latitude = restaurantKanban.getRestaurantTmpl().getPosition().getCoordinate().getY();

            searchDelivery(orderId, longitude, latitude);
        }

    }

    @Async
    void searchDelivery(Integer orderId, double longitude, double latitude) {

        KafkaTopicOrderDTO.Restaurant restaurant = new KafkaTopicOrderDTO.Restaurant();
        restaurant.setLatitude(latitude);
        restaurant.setLongitude(longitude);

        KafkaTopicOrderDTO topicOrderDTO = new KafkaTopicOrderDTO();
        topicOrderDTO.setAction(TOPIC_ORDER_ACTION.SEARCH_DELIVERY);
        topicOrderDTO.setRestaurant(restaurant);
        topicOrderDTO.setId(orderId);

        kafkaTopicOrderProducer.sendMessageTopicOrder(topicOrderDTO);
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
}
