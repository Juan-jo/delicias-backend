package com.delivery.app.mobile.user.service;

import com.delicias.kafka.core.dto.KafkaTopicKanbanDTO;
import com.delicias.kafka.core.dto.KafkaTopicOrderDTO;
import com.delicias.kafka.core.enums.TOPIC_ORDER_ACTION;
import com.delivery.app.configs.DeliciasAppProperties;
import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.kafka.producer.KafkaTopicKanbanProducer;
import com.delivery.app.kafka.producer.KafkaTopicOrderProducer;
import com.delivery.app.mobile.user.dtos.MobileUserCreateOrderDTO;
import com.delivery.app.mobile.user.exception.MobileOrderDifferentSubtotalAmount;
import com.delivery.app.mobile.user.exception.MobileOrderDifferentTotalAmount;
import com.delivery.app.pos.enums.KanbanStatus;
import com.delivery.app.pos.enums.OrderStatus;
import com.delivery.app.pos.order.models.PosOrder;
import com.delivery.app.pos.order.models.PosOrderLine;
import com.delivery.app.pos.order.models.PosOrderLineProductAttributeValueRel;
import com.delivery.app.pos.order.repositories.PosOrderLineProductAttributeValueRelRepository;
import com.delivery.app.pos.order.repositories.PosOrderLineRepository;
import com.delivery.app.pos.order.repositories.PosOrderRepository;
import com.delivery.app.pos.restaurant_kanban.model.PosRestaurantKanban;
import com.delivery.app.pos.restaurant_kanban.repository.PosRestaurantKanbanRepository;
import com.delivery.app.product.attribute.models.ProductAttributeValue;
import com.delivery.app.product.template.models.ProductTemplate;
import com.delivery.app.product.template.repositories.ProductTemplateRepository;
import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import com.delivery.app.restaurant.template.repository.RestaurantTemplateRepository;
import com.delivery.app.security.services.AuthenticationFacade;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;


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

    private static final double costService = 35.00;

    @Transactional
    public void create(MobileUserCreateOrderDTO createOrderDTO) {

        List<ProductTemplate> tmpls = getProductTemplatesFromDatabase(createOrderDTO);

        UUID userId = authenticationFacade.userId();

        PosOrder newOrder = createPosOrder(createOrderDTO, userId);

        double subtotal = 0.0;

        List<ProductOrder> products = new ArrayList<>();

        for(MobileUserCreateOrderDTO.ProductTmpl productTmpl: createOrderDTO.productTmpl()) {

            double totalAmountAttrValues =  0.0;

            ProductTemplate currentTmpl = tmpls.stream()
                    .filter(tm -> tm.getId().equals(productTmpl.id()) && tm.getRestaurantTmpl().getId().equals(createOrderDTO.restaurantId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("ProductTmpl", "id", productTmpl.id()));

            List<Integer> attrValuesId = Optional.ofNullable(productTmpl.attrValues()).orElse(List.of()).stream()
                    .map(MobileUserCreateOrderDTO.AttrValue::id).toList();

            List<ProductAttrValue> productAtrValues = currentTmpl.getAttributeValues().stream()
                    .filter(attrVal -> attrValuesId.contains(attrVal.getId()))
                    .map(i -> new ProductAttrValue(i.getId(), i.getExtraPrice()))
                    .toList();

            totalAmountAttrValues += productAtrValues.stream()
                    .map(av -> av.extraPrice)
                    .reduce(0.0, Double::sum) * productTmpl.qty();

            subtotal += totalAmountAttrValues + (productTmpl.qty() * currentTmpl.getListPrice());


            saveProductLine(newOrder, productTmpl, currentTmpl.getListPrice(), subtotal, productAtrValues);

            products.add(new ProductOrder(currentTmpl.getName(), productTmpl.qty()));
        }

        validateAmounts(createOrderDTO, subtotal);

        PosRestaurantKanban kanban = createKanban(newOrder);

        RestaurantTemplate restaurantTemplate = restaurantTemplateRepository.findById(createOrderDTO.restaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("restaurant", "id", createOrderDTO.restaurantId()));

        // Send Kafka Messages
        sendMessageKafkaTopicKanban(createOrderDTO, kanban);
        sendMessageKafkaTopicOrder(
                newOrder.getId(),
                newOrder.getStatus().name(),
                userId,
                restaurantTemplate,
                products);

    }

    private PosOrder createPosOrder(MobileUserCreateOrderDTO createOrderDTO, UUID userId) {

        LocalDate today = ZonedDateTime.now(deliciasAppProperties.getZoneOffset()).toLocalDate();

        return posOrderRepository.save(
                PosOrder.builder()
                        .status(OrderStatus.ORDERED)
                        .amountTotal(amount2f(createOrderDTO.total()))
                        .amountSubtotal(amount2f(createOrderDTO.subtotal()))
                        .costService(amount2f(createOrderDTO.costService()))
                        .amountDiscount(amount2f(createOrderDTO.discount()))
                        .userUID(userId)
                        .userId(userId)
                        .dateOrder(today)
                        .restaurantTmpl(new RestaurantTemplate(createOrderDTO.restaurantId()))
                        .notes(createOrderDTO.notes())
                        .build()
        );
    }

    private void saveProductLine(PosOrder posOrder,
                                 MobileUserCreateOrderDTO.ProductTmpl productTmpl,
                                 Double priceUnit,
                                 Double priceTotal,
                                 List<ProductAttrValue> attrValues) {

        PosOrderLine newLine = posOrderLineRepository.save(PosOrderLine.builder()
                .order(posOrder)
                .productTemplate(new ProductTemplate(productTmpl.id()))
                .quantity(productTmpl.qty())
                .priceUnit(priceUnit)
                .priceTotal(amount2f(priceTotal))
                .build());

        saveProductLineAttrValue(newLine, attrValues);
    }

    private void saveProductLineAttrValue(PosOrderLine line,
                                          List<ProductAttrValue> attrValues) {

        posOrderLineProductAttributeValueRelRepository.saveAll(
          attrValues.stream().map(i -> PosOrderLineProductAttributeValueRel.builder()
                  .line(line)
                  .attributeValue(new ProductAttributeValue(i.id))
                  .extraPrice(i.extraPrice)
                  .build()).toList()
        );
    }

    private PosRestaurantKanban createKanban(PosOrder posOrder) {
         return posRestaurantKanbanRepository.save(
                PosRestaurantKanban.builder()
                        .status(KanbanStatus.RECEIVED)
                        .order(posOrder)
                        .restaurantTmpl(posOrder.getRestaurantTmpl())
                        .build()
        );


    }

    private List<ProductTemplate> getProductTemplatesFromDatabase(MobileUserCreateOrderDTO createOrderDTO) {
        return productTemplateRepository.findByIdIn(
                createOrderDTO.productTmpl().stream()
                        .map(MobileUserCreateOrderDTO.ProductTmpl::id)
                        .toList()
        );
    }

    private void sendMessageKafkaTopicOrder(
            Integer orderId,
            String status,
            UUID userId,
            RestaurantTemplate restaurantTmpl,
            List<ProductOrder> orderLines
    ) {

        List<KafkaTopicOrderDTO.OrderLine> products = orderLines.stream()
                .map(line -> new KafkaTopicOrderDTO.OrderLine(line.qty, line.name)).toList();

        KafkaTopicOrderDTO.OrderRestaurant restaurant = new KafkaTopicOrderDTO.OrderRestaurant(
                restaurantTmpl.getName(),
                restaurantTmpl.getAddress(),
                Optional.ofNullable(restaurantTmpl.getImageLogo())
                        .map(p-> String.format("%s/%s", deliciasAppProperties.getFiles().getResources(), p))
                        .orElse(deliciasAppProperties.getFiles().getStaticDefault()),
                new KafkaTopicOrderDTO.GpsPoint(
                        restaurantTmpl.getPosition().getCoordinate().getY(),
                        restaurantTmpl.getPosition().getCoordinate().getX())
        );

        //TODO Falta obtener usuario
        KafkaTopicOrderDTO.OrderUser user = new KafkaTopicOrderDTO.OrderUser(
                "Juan Jośe",
                "Zocuiteco Benito Juarez, Mexico",
                "https://images.freeimages.com/365/images/previews/85b/psd-universal-blue-web-user-icon-53242.jpg",
                new KafkaTopicOrderDTO.GpsPoint(0d, 0d)
        );


        KafkaTopicOrderDTO kafkaTopicOrderDTO = new KafkaTopicOrderDTO();
        kafkaTopicOrderDTO.setAction(TOPIC_ORDER_ACTION.USER_ORDER_CREATED);
        kafkaTopicOrderDTO.setOrder(
                new KafkaTopicOrderDTO.Order(
                        orderId,
                        userId.toString(),
                        status,
                        new Date(),
                        products,
                        restaurant,
                        user
                )
        );

        kafkaTopicOrderProducer.sendMessageTopicOrder(kafkaTopicOrderDTO);
    }

    private void sendMessageKafkaTopicKanban(
            MobileUserCreateOrderDTO createOrderDTO,
            PosRestaurantKanban kanban
    ) {
        KafkaTopicKanbanDTO kafkaTopicKanbanDTO = new KafkaTopicKanbanDTO();
        kafkaTopicKanbanDTO.setRestaurantId(createOrderDTO.restaurantId());
        kafkaTopicKanbanDTO.setKanbanId(kanban.getId());

        kafkaTopicKanbanProducer.sendMessageTopicKanban(
                kafkaTopicKanbanDTO
        );
    }


    private void validateAmounts(MobileUserCreateOrderDTO createOrderDTO, double subtotal) {
        if(amount2f(subtotal) != createOrderDTO.subtotal()) {
            throw new MobileOrderDifferentSubtotalAmount(subtotal, createOrderDTO.subtotal());
        }

        double total = amount2f(subtotal + costService);

        double totalWithoutDiscount = createOrderDTO.total() - createOrderDTO.discount();

        if(totalWithoutDiscount != total) {
            throw new MobileOrderDifferentTotalAmount(total, totalWithoutDiscount);
        }
    }

    // double value two decimal
    private double amount2f(Double val) {

        return Double.parseDouble(String.format("%.2f", val));
    }


    private record ProductAttrValue(
            Integer id,
            Double extraPrice
    ) { }

    private record ProductOrder(
            String name,
            Integer qty
    ) { }
}
