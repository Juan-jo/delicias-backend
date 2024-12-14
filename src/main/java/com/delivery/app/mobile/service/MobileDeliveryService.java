package com.delivery.app.mobile.service;

import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.mobile.dtos.DeliveryOrderLastPosition;
import com.delivery.app.mobile.dtos.MobileDeliveryOrderDetailDTO;
import com.delivery.app.pos.enums.OrderStatus;
import com.delivery.app.pos.order.models.PosOrder;
import com.delivery.app.pos.order.repositories.PosOrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class MobileDeliveryService {

    private final PosOrderRepository posOrderRepository;

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

        OrderRoadToStore(orderId);
    }

    public void roadToSDelivery(Integer orderId) {
        PosOrder order = posOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("order", "id", orderId));
    }

    @Async
    public void updateGpsTracking(DeliveryOrderLastPosition orderLastPosition) {

        PosOrder order = posOrderRepository.findById(orderLastPosition.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("order", "id", orderLastPosition.orderId()));

        if(order.getStatus().equals(OrderStatus.DELIVERY_ROAD_TO_DESTINATION)) {

        }

    }


    private void OrderRoadToStore(Integer orderId) {

    }


}
