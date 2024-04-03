package com.delivery.app.pos.order.service;

import com.delivery.app.pos.order.dto.CreateOrderRequestDTO;
import com.delivery.app.pos.order.dto.FilterOrdersRestaurantRequestDTO;
import com.delivery.app.pos.order.dto.FilterOrdersRestaurantResponseDTO;
import com.delivery.app.pos.order.dto.OrderDTO;
import com.delivery.app.pos.order.models.PosOrder;
import com.delivery.app.pos.order.repository.PosOrderRepository;
import com.delivery.app.pos.status.OrderStatus;
import com.delivery.app.product.template.models.ProductTemplate;
import com.delivery.app.product.template.repositories.ProductTemplateRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class PosOrderService {

    private final PosOrderRepository posOrderRepository;
    private final ProductTemplateRepository productTemplateRepository;



    @Transactional
    public void create(CreateOrderRequestDTO orderDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Double amountTotal = 0d;

        List<ProductTemplate> productTemplates =
                productTemplateRepository.findByIdIn(orderDTO.tmplId().stream().toList());


        for(ProductTemplate template: productTemplates) {
            amountTotal += template.getListPrice();

        }

        posOrderRepository.save(
                PosOrder.builder()
                        .status(OrderStatus.ORDERED)
                        .amountTotal(amountTotal)
                        .userUID(UUID.fromString(authentication.getName()))
                        .dateOrder(LocalDate.now())
                        .build()
        );
    }

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
                .orderDate(i.getDateOrder())
                .createdAt(i.getCreatedAt())
                .build());
    }
}
