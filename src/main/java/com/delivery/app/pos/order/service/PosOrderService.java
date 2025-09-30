package com.delivery.app.pos.order.service;

import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.pos.order.dto.CreateOrderRequestDTO;
import com.delivery.app.pos.order.dto.FilterOrdersRestaurantRequestDTO;
import com.delivery.app.pos.order.dto.FilterOrdersRestaurantResponseDTO;
import com.delivery.app.pos.order.dto.OrderDTO;
import com.delivery.app.pos.order.repositories.PosOrderRepository;
import com.delivery.app.product.attribute.models.ProductAttributeValue;
import com.delivery.app.product.template.models.ProductTemplate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@AllArgsConstructor
@Service
public class PosOrderService {

    private final PosOrderRepository posOrderRepository;

    public OrderDTO findById(Integer orderId) {

        return posOrderRepository.findById(orderId)
                .map(i -> OrderDTO.builder()
                        .id(i.getId())
                        .status(i.getStatus())
                        .build())
                .orElseThrow();
    }

    @Transactional(readOnly = true)
    public Page<FilterOrdersRestaurantResponseDTO> filterRestaurant(
            FilterOrdersRestaurantRequestDTO requestDTO) {

        return this.posOrderRepository.filterRestaurant(
                requestDTO.getStatus(),
                requestDTO.pageable()
        ).map(i -> FilterOrdersRestaurantResponseDTO.builder()
                .id(i.getId())
                .status(i.getStatus())
                .build());
    }




}
