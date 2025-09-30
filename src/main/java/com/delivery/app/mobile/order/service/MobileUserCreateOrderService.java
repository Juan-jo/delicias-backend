package com.delivery.app.mobile.order.service;

import com.delicias.soft.services.core.common.OrderStatus;
import com.delicias.soft.services.core.supabase.order.dto.SupabaseOrderDTO;
import com.delicias.soft.services.core.supabase.order.dto.SupabaseOrderLineDTO;
import com.delicias.soft.services.core.supabase.order.service.CoreSupabaseOrderService;
import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.mobile.shopping.dto.MobileShoppingCartDTO;
import com.delivery.app.mobile.shopping.service.MobileShoppingCartService;
import com.delivery.app.mobile.user.dtos.MobileUserCreateOrderDTO;
import com.delivery.app.pos.order.models.PosOrder;
import com.delivery.app.pos.order.models.PosOrderAdjustment;
import com.delivery.app.pos.order.models.PosOrderLine;
import com.delivery.app.pos.order.models.PosOrderLineProductAttributeValueRel;
import com.delivery.app.pos.order.repositories.PosOrderRepository;
import com.delivery.app.product.attribute.models.ProductAttributeValue;
import com.delivery.app.product.template.models.ProductTemplate;
import com.delivery.app.product.template.repositories.ProductTemplateRepository;
import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import com.delivery.app.restaurant.template.repository.RestaurantTemplateRepository;
import com.delivery.app.security.model.UserAddress;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class MobileUserCreateOrderService {

    private final ProductTemplateRepository productTemplateRepository;
    private final PosOrderRepository posOrderRepository;

    private final RestaurantTemplateRepository restaurantTemplateRepository;
    private final MobileShoppingCartService mobileShoppingCartService;
    private final CoreSupabaseOrderService coreSupabaseOrderService;


    @Transactional
    public void create(MobileUserCreateOrderDTO createOrderRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UUID userId = UUID.fromString(authentication.getName());

        MobileShoppingCartDTO shoppingCartDTO = mobileShoppingCartService.findById(createOrderRequestDTO.shoppingCartId());

        RestaurantTemplate restaurantTmpl = restaurantTemplateRepository.findById(shoppingCartDTO.restaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantTmpl", "id", shoppingCartDTO.restaurantId()));

        List<ProductTemplate> productTemplatesAdded = productTemplateRepository.findByIdIn(
                shoppingCartDTO.shoppingLines().stream()
                        .map(MobileShoppingCartDTO.ShoppingLine::productTmplId)
                        .toList()
        );

        PosOrder newOrder = PosOrder.builder()
                .status(OrderStatus.ORDERED)
                .amountSubtotal(shoppingCartDTO.subtotal())
                .amountTotal(shoppingCartDTO.total())
                .userUID(userId)
                .dateOrder(LocalDate.now())
                .restaurantTmpl(restaurantTmpl)
                .notes(createOrderRequestDTO.notes())
                .userAddress(UserAddress.builder()
                        .id(shoppingCartDTO.deliveryAddress().id())
                        .build())
                .adjustments(shoppingCartDTO.charges().stream().map(ch ->
                        PosOrderAdjustment.builder()
                                .name(ch.name())
                                .type(ch.adjustmentType())
                                .amount(ch.amount())
                                .build())
                        .toList())
                .build();


        for (MobileShoppingCartDTO.ShoppingLine line : shoppingCartDTO.shoppingLines()) {

            ProductTemplate productTmpl = productTemplatesAdded.stream()
                    .filter(it -> Objects.equals(it.getId(), line.productTmplId()))
                    .findAny()
                    .orElseThrow(() -> new ResourceNotFoundException("ProductTmpl", "id", line.productTmplId()));

            PosOrderLine orderLine = PosOrderLine.builder()
                    .productTemplate(productTmpl)
                    .quantity(line.qty())
                    .priceUnit(line.priceUnit())
                    .priceTotal(line.priceTotal())
                    .build();

            // Agregar valores de atributos (extras)
            Optional.ofNullable(line.attrValuesAdded()).ifPresent(attrValues -> {
                attrValues.forEach(attributeValueId -> {
                    Double extraPrice = productTmpl.getAttributeValues().stream()
                            .filter(attr -> attr.getId().equals(attributeValueId))
                            .map(ProductAttributeValue::getExtraPrice)
                            .findFirst()
                            .orElse(0d);

                    PosOrderLineProductAttributeValueRel attrRel = PosOrderLineProductAttributeValueRel.builder()
                            .attributeValue(new ProductAttributeValue(attributeValueId))
                            .extraPrice(extraPrice)
                            .build();

                    orderLine.addAttributeValue(attrRel);
                });
            });

            newOrder.addLine(orderLine);
        }

        posOrderRepository.saveAndFlush(newOrder);

        posOrderRepository.refresh(newOrder);

        saveOrderInSupabase(newOrder);
    }

    private void saveOrderInSupabase(PosOrder order) {

        // Save Order
        coreSupabaseOrderService.saveOrder(
                SupabaseOrderDTO.builder()
                        .id(order.getId())
                        .userId(order.getUserUID())
                        .status(order.getStatus().name())
                        .restaurantId(order.getRestaurantTmpl().getId())
                        .build()
        );

        // Save Lines
        coreSupabaseOrderService.saveLines(
                order.getLines().stream().map(line -> {

                    String desc = "";

                    if(line.getAttributeValues() != null) {

                        desc = line.getAttributeValues().stream()
                                        .map(
                                                r -> String.format("%s: %s, \n", r.getAttributeValue().getAttribute().getName(), r.getAttributeValue().getName()))
                                        .collect(Collectors.joining(". \n"));
                    }

                    return SupabaseOrderLineDTO.builder()
                            .id(line.getId())
                            .productName(line.getProductTemplate().getName())
                            .quantity(line.getQuantity())
                            .price(line.getPriceTotal())
                            .description(desc)
                            .orderId(line.getOrder().getId())
                            .build();

                }).collect(Collectors.toSet())
        );
    }

    public void supabaseCreateOrder(Integer orderId) {

        PosOrder order = this.posOrderRepository.findById(orderId)
                .orElseThrow();

        saveOrderInSupabase(order);

    }

}
