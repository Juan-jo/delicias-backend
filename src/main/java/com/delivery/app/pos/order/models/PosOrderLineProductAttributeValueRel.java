package com.delivery.app.pos.order.models;

import com.delivery.app.product.attribute.models.ProductAttributeValue;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pos_order_line_product_attribute_value_rel")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PosOrderLineProductAttributeValueRel {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "pos_order_line_product_attribute_value_rel_id_seq")
    @SequenceGenerator(
            name = "pos_order_line_product_attribute_value_rel_id_seq",
            allocationSize = 1
    )
    private Integer id;


    @Column(name = "default_extra_price")
    private Double extraPrice;

    @ManyToOne
    @JoinColumn(name = "order_line_id", referencedColumnName = "id")
    private PosOrderLine line;

    @ManyToOne
    @JoinColumn(name = "product_attribute_value_id", referencedColumnName = "id")
    private ProductAttributeValue attributeValue;
}
