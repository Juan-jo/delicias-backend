package com.delivery.app.mobile.service;

import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.kafka.dto.KafkaRestaurantKanbanDTO;
import com.delivery.app.kafka.producer.KafkaOrderProducer;
import com.delivery.app.mobile.dtos.MobileCreateOrderDTO;
import com.delivery.app.mobile.exception.MobileOrderDifferentSubtotalAmount;
import com.delivery.app.mobile.exception.MobileOrderDifferentTotalAmount;
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
import com.delivery.app.security.services.AuthenticationFacade;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
public class MobileCreateOrderService {

    private final ProductTemplateRepository productTemplateRepository;
    private final AuthenticationFacade authenticationFacade;
    private final PosOrderRepository posOrderRepository;
    private final PosOrderLineRepository posOrderLineRepository;
    private final PosOrderLineProductAttributeValueRelRepository posOrderLineProductAttributeValueRelRepository;
    private final PosRestaurantKanbanRepository posRestaurantKanbanRepository;
    private final KafkaOrderProducer kafkaProducer;

    private static final double costService = 35.00;

    @Transactional
    public void create(MobileCreateOrderDTO createOrderDTO) {

        List<ProductTemplate> tmpls = getProductTemplatesFromDatabase(createOrderDTO);

        UUID userId = authenticationFacade.userId();

        PosOrder newOrder = generateNewPosOrder(createOrderDTO, userId);

        double subtotal = 0.0;

        for(MobileCreateOrderDTO.ProductTmpl productTmpl: createOrderDTO.productTmpl()) {

            double totalAmountAttrValues =  0.0;

            ProductTemplate currentTmpl = tmpls.stream()
                    .filter(tm -> tm.getId().equals(productTmpl.id()) && tm.getRestaurantTmpl().getId().equals(createOrderDTO.restaurantId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("ProductTmpl", "id", productTmpl.id()));

            List<Integer> attrValuesId = productTmpl.attrValues().stream()
                    .map(MobileCreateOrderDTO.AttrValue::id).toList();

            List<ProductAttrValue> productAtrValues = currentTmpl.getAttributeValues().stream()
                    .filter(attrVal -> attrValuesId.contains(attrVal.getId()))
                    .map(i -> new ProductAttrValue(i.getId(), i.getExtraPrice()))
                    .toList();

            totalAmountAttrValues += productAtrValues.stream()
                    .map(av -> av.extraPrice)
                    .reduce(0.0, Double::sum) * productTmpl.qty();

            subtotal += totalAmountAttrValues + (productTmpl.qty() * currentTmpl.getListPrice());


            saveProductLine(newOrder, productTmpl, currentTmpl.getListPrice(), subtotal, productAtrValues);
        }

        validateAmounts(createOrderDTO, subtotal);

        addRestaurantKanban(newOrder);

        kafkaProducer.sendMessage(
                KafkaRestaurantKanbanDTO.builder()
                        .orderId(newOrder.getId())
                        .restaurantId(createOrderDTO.restaurantId())
                        .build()
                //String.valueOf(newOrder.getId())
        );
    }


    private PosOrder generateNewPosOrder(MobileCreateOrderDTO createOrderDTO, UUID userId) {
        return posOrderRepository.save(
                PosOrder.builder()
                        .status(OrderStatus.ORDERED)
                        .amountTotal(amount2f(createOrderDTO.total()))
                        .amountSubtotal(amount2f(createOrderDTO.subtotal()))
                        .costService(amount2f(createOrderDTO.costService()))
                        .amountDiscount(amount2f(createOrderDTO.discount()))
                        .userUID(userId)
                        .userId(userId)
                        .dateOrder(LocalDate.now())
                        .restaurantTmpl(new RestaurantTemplate(createOrderDTO.restaurantId()))
                        .notes(createOrderDTO.notes())
                        .build()
        );
    }

    private void saveProductLine(PosOrder posOrder,
                                 MobileCreateOrderDTO.ProductTmpl productTmpl,
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


    private void  addRestaurantKanban(PosOrder posOrder) {
        posRestaurantKanbanRepository.save(
                PosRestaurantKanban.builder()
                        .status(KanbanStatus.RECEIVED)
                        .order(posOrder)
                        .restaurantTmpl(posOrder.getRestaurantTmpl())
                        .build()
        );
    }
    private List<ProductTemplate> getProductTemplatesFromDatabase(MobileCreateOrderDTO createOrderDTO) {
        return productTemplateRepository.findByIdIn(
                createOrderDTO.productTmpl().stream()
                        .map(MobileCreateOrderDTO.ProductTmpl::id)
                        .toList()
        );
    }



    private void validateAmounts(MobileCreateOrderDTO createOrderDTO, double subtotal) {
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
}
