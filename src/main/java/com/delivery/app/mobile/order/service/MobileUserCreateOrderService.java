package com.delivery.app.mobile.order.service;

import com.delivery.app.configs.DeliciasAppProperties;
import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.kafka.producer.KafkaTopicKanbanProducer;
import com.delivery.app.kafka.producer.KafkaTopicOrderProducer;
import com.delivery.app.mobile.shopping.dto.MobileShoppingCartDTO;
import com.delivery.app.mobile.shopping.service.MobileShoppingCartService;
import com.delivery.app.mobile.user.dtos.MobileUserCreateOrderDTO;
import com.delivery.app.pos.enums.OrderStatus;
import com.delivery.app.pos.kanban.repository.PosRestaurantKanbanRepository;
import com.delivery.app.pos.order.models.PosOrder;
import com.delivery.app.pos.order.models.PosOrderAdjustment;
import com.delivery.app.pos.order.models.PosOrderLine;
import com.delivery.app.pos.order.models.PosOrderLineProductAttributeValueRel;
import com.delivery.app.pos.order.repositories.PosOrderLineProductAttributeValueRelRepository;
import com.delivery.app.pos.order.repositories.PosOrderLineRepository;
import com.delivery.app.pos.order.repositories.PosOrderRepository;
import com.delivery.app.product.attribute.models.ProductAttributeValue;
import com.delivery.app.product.template.models.ProductTemplate;
import com.delivery.app.product.template.repositories.ProductTemplateRepository;
import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import com.delivery.app.restaurant.template.repository.RestaurantTemplateRepository;
import com.delivery.app.security.model.UserAddress;
import com.delivery.app.security.services.AuthenticationFacade;
import com.delivery.app.security.services.KeycloakUserService;
import com.delivery.app.supabase.order.service.SupOrderService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class MobileUserCreateOrderService {

    private final ProductTemplateRepository productTemplateRepository;
    private final AuthenticationFacade authenticationFacade;
    private final PosOrderRepository posOrderRepository;
    private final PosOrderLineRepository posOrderLineRepository;
    private final PosOrderLineProductAttributeValueRelRepository posOrderLineProductAttributeValueRelRepository;
    private final PosRestaurantKanbanRepository posRestaurantKanbanRepository;
    private final KafkaTopicKanbanProducer kafkaTopicKanbanProducer;
    private final KafkaTopicOrderProducer kafkaTopicOrderProducer;
    private final DeliciasAppProperties deliciasAppProperties;
    private final RestaurantTemplateRepository restaurantTemplateRepository;
    private final MobileShoppingCartService mobileShoppingCartService;
    private final KeycloakUserService keycloakUserService;
    private final SupOrderService supOrderService;


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

        //supOrderService.createOrder(newOrder);
    }


    @Transactional
    public void create3(MobileUserCreateOrderDTO createOrderRequestDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        MobileShoppingCartDTO shoppingCartDTO = mobileShoppingCartService.findById(createOrderRequestDTO.shoppingCartId());

        RestaurantTemplate restaurantTmpl = restaurantTemplateRepository.findById(shoppingCartDTO.restaurantId())
                .orElseThrow(()-> new ResourceNotFoundException("RestaurantTmpl", "id", shoppingCartDTO.restaurantId()));

        List<ProductTemplate> productTemplatesAdded = productTemplateRepository.findByIdIn
                (shoppingCartDTO.shoppingLines().stream().map(MobileShoppingCartDTO.ShoppingLine::productTmplId).toList());

        UUID userId = UUID.fromString(authentication.getName());


        PosOrder newOrder = posOrderRepository.save(
                PosOrder.builder()
                        .status(OrderStatus.ORDERED)
                        .amountTotal(shoppingCartDTO.total())
                        .userUID(userId)
                        .dateOrder(LocalDate.now())
                        .restaurantTmpl(restaurantTmpl)
                        .build()
        );
        List<PosOrderLine> lines = new ArrayList<>();

        for (MobileShoppingCartDTO.ShoppingLine line: shoppingCartDTO.shoppingLines()) {

            ProductTemplate productTmpl = productTemplatesAdded.stream()
                    .filter(it -> Objects.equals(it.getId(), line.productTmplId())).findAny()
                    .orElseThrow(() -> new ResourceNotFoundException("ProductTmpl", "id", line.productTmplId()));

            PosOrderLine orderLine = posOrderLineRepository.save(PosOrderLine.builder()
                    .order(newOrder)
                    .productTemplate(productTmpl)
                    .quantity(line.qty())
                    .priceUnit(line.priceUnit())
                    .priceTotal(line.priceTotal()) // sum price unit and all attributes values
                    .build());

            lines.add(orderLine);

            Optional.ofNullable(line.attrValuesAdded()).ifPresent(attrValues -> {

                posOrderLineProductAttributeValueRelRepository.saveAll(attrValues.stream().map(
                        attributeValueId -> PosOrderLineProductAttributeValueRel.builder()
                                .line(orderLine)
                                .attributeValue(new ProductAttributeValue(attributeValueId))
                                .extraPrice(
                                        productTmpl.getAttributeValues().stream()
                                                .filter(k -> k.getId().equals(attributeValueId))
                                                .findAny()
                                                .map(ProductAttributeValue::getExtraPrice)
                                                .orElse(0d)
                                )
                                .build()
                ).collect(Collectors.toSet()));

            });
        }


        supOrderService.createOrder(newOrder);
    }

    public void supabaseCreateOrder(Integer orderId) {

        PosOrder order = this.posOrderRepository.findById(orderId)
                .orElseThrow();

        supOrderService.createOrder(order);

    }

}
