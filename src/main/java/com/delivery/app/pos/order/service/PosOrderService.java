package com.delivery.app.pos.order.service;

import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.pos.order.dto.CreateOrderRequestDTO;
import com.delivery.app.pos.order.dto.FilterOrdersRestaurantRequestDTO;
import com.delivery.app.pos.order.dto.FilterOrdersRestaurantResponseDTO;
import com.delivery.app.pos.order.dto.OrderDTO;
import com.delivery.app.pos.order.models.PosOrder;
import com.delivery.app.pos.order.models.PosOrderLine;
import com.delivery.app.pos.order.models.PosOrderLineProductAttributeValueRel;
import com.delivery.app.pos.order.repositories.PosOrderLineProductAttributeValueRelRepository;
import com.delivery.app.pos.order.repositories.PosOrderLineRepository;
import com.delivery.app.pos.order.repositories.PosOrderRepository;
import com.delivery.app.pos.enums.OrderStatus;
import com.delivery.app.product.attribute.models.ProductAttributeValue;
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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PosOrderService {

    private final PosOrderRepository posOrderRepository;
    private final ProductTemplateRepository productTemplateRepository;
    private final PosOrderLineProductAttributeValueRelRepository posOrderLineProductAttributeValueRelRepository;
    private final PosOrderLineRepository posOrderLineRepository;


    @Transactional
    public void create(CreateOrderRequestDTO createOrderRequestDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<ProductTemplate> productTemplates =
                productTemplateRepository.findByIdIn(createOrderRequestDTO.productTemplates().stream()
                        .map(CreateOrderRequestDTO.RequestOrderProductTemplate::id).toList());


        double amountTotal = calculateAmountTotal(productTemplates, createOrderRequestDTO);


        PosOrder newOrder = posOrderRepository.save(
                PosOrder.builder()
                        .status(OrderStatus.ORDERED)
                        .amountTotal(amountTotal)
                        .userUID(UUID.fromString(authentication.getName()))
                        .dateOrder(LocalDate.now())
                        .build()
        );


        for (CreateOrderRequestDTO.RequestOrderProductTemplate productTemplateDTO : createOrderRequestDTO.productTemplates()) {

            ProductTemplate template = productTemplates.stream()
                    .filter(tmpl -> productTemplateDTO.id().equals(tmpl.getId()))
                    .findAny()
                    .orElseThrow(() -> new ResourceNotFoundException("template", "id", productTemplateDTO.id()));


            PosOrderLine orderLine = posOrderLineRepository.save(PosOrderLine.builder()
                    .order(newOrder)
                    .productTemplate(template)
                    .quantity(productTemplateDTO.qty())
                    .priceUnit(template.getListPrice())
                    .priceTotal(template.getListPrice() * productTemplateDTO.qty())
                    .build());


            Optional.ofNullable(productTemplateDTO.attributeValues()).ifPresent(attrValues -> {

                posOrderLineProductAttributeValueRelRepository.saveAll(attrValues.stream().map(
                        attributeValueId -> PosOrderLineProductAttributeValueRel.builder()
                                .line(orderLine)
                                .attributeValue(new ProductAttributeValue(attributeValueId))
                                .extraPrice(
                                        template.getAttributeValues().stream()
                                                .filter(k -> k.getId().equals(attributeValueId))
                                                .findAny()
                                                .map(ProductAttributeValue::getExtraPrice)
                                                .orElse(0d)
                                )
                                .build()
                ).collect(Collectors.toSet()));

            });

        }

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
                .build());
    }



    private static double calculateAmountTotal(
            List<ProductTemplate> productTemplates, CreateOrderRequestDTO createOrderRequestDTO) {

        return createOrderRequestDTO.productTemplates().stream()
                .mapToDouble(tmplDTO -> {

                    ProductTemplate template = productTemplates.stream()
                            .filter(tmpl -> tmpl.getId().equals(tmplDTO.id()))
                            .findAny()
                            .orElseThrow(() -> new ResourceNotFoundException("template", "id", tmplDTO.id()));

                    AtomicReference<Double> totalAttributesValue = new AtomicReference<>((double) 0);

                    Optional.ofNullable(tmplDTO.attributeValues()).ifPresent(attrValues-> {

                        totalAttributesValue.set(attrValues.stream()
                                .mapToDouble(attrId -> template.getAttributeValues().stream()
                                        .filter(attr -> attr.getId().equals(attrId))
                                        .findAny()
                                        .map(ProductAttributeValue::getExtraPrice)
                                        .orElse(0d))
                                .sum());
                    });

                    return (template.getListPrice() * tmplDTO.qty()) + totalAttributesValue.get();
                }).sum();
    }
}
