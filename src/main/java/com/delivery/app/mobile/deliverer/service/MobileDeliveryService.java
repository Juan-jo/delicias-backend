package com.delivery.app.mobile.deliverer.service;

import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.mobile.deliverer.dto.DeliveryOrderLastPosition;
import com.delivery.app.mobile.deliverer.dto.MobileDeliveryOrderDetailDTO;
import com.delivery.app.pos.enums.OrderStatus;
import com.delivery.app.pos.order.models.PosOrder;
import com.delivery.app.pos.order.repositories.PosOrderRepository;
import com.delivery.app.security.services.AuthenticationFacade;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class MobileDeliveryService {

    private final PosOrderRepository posOrderRepository;
    private final JdbcTemplate jdbcTemplate;
    private final AuthenticationFacade authenticationFacade;

    public MobileDeliveryOrderDetailDTO detail(Integer orderId) {

        PosOrder order = posOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("order", "id", orderId));


        return MobileDeliveryOrderDetailDTO.builder()
                .orderId(order.getId())
                .restaurant(MobileDeliveryOrderDetailDTO.Restaurant.builder()
                        .id(order.getRestaurantTmpl().getId())
                        .name(order.getRestaurantTmpl().getName())
                        .street(order.getRestaurantTmpl().getAddress())
                        .picture(order.getRestaurantTmpl().getImageLogo())
                        .build())
                .userInfo(MobileDeliveryOrderDetailDTO.UserInfo.builder()
                        .build())
                .build();
    }


    public void roadToStore(Integer orderId) {
        PosOrder order = posOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("order", "id", orderId));

        order.setStatus(OrderStatus.DELIVERY_ROAD_TO_STORE);
        posOrderRepository.save(order);

    }

    @Async
    public void updateGpsTracking(DeliveryOrderLastPosition orderLastPosition) {

        PosOrder order = posOrderRepository.findById(orderLastPosition.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("order", "id", orderLastPosition.orderId()));

        if(
                order.getStatus().equals(OrderStatus.DELIVERY_ROAD_TO_DESTINATION) ||
                order.getStatus().equals(OrderStatus.DELIVERY_ROAD_TO_STORE)
        ) {

            UUID userId = authenticationFacade.userId();

            jdbcTemplate.update("""
                    UPDATE      deliverers
                        SET     last_position = ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography
                        WHERE   deliverer_id = ?::uuid;
                    """, orderLastPosition.longitude(), orderLastPosition.latitude(), userId);

            Optional.ofNullable(order.getDeliveryOrderRelId()).ifPresent(orderRelId -> {

                jdbcTemplate.update("""
                    UPDATE      delivery_order_rel
                        SET     last_position = ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography
                        WHERE   id = ?;
                    """, orderLastPosition.longitude(), orderLastPosition.latitude(), orderRelId);


            });

        }

    }





}
