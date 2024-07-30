package com.delivery.app.pos.order.models;

import com.delivery.app.configs.auditable.model.AuditableEntity;
import com.delivery.app.product.attribute.models.ProductAttributeValue;
import com.delivery.app.product.template.models.ProductTemplate;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pos_order_line")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PosOrderLine extends AuditableEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "pos_order_line_id_seq")
    @SequenceGenerator(
            name = "pos_order_line_id_seq",
            allocationSize = 1
    )
    private Integer id;


    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private PosOrder order;

    @ManyToOne
    @JoinColumn(name = "product_tmpl_id", referencedColumnName = "id")
    private ProductTemplate productTemplate;

    @Column(name = "qty")
    private Integer quantity;

    @Column(name = "price_unit")
    private Double priceUnit;

    @Column(name = "price_total")
    private Double priceTotal;

    /*@ManyToMany
    @JoinTable(
            name = "pos_order_line_product_attribute_value_rel",
            joinColumns = @JoinColumn(name = "id"),
            foreignKey = @ForeignKey(name = "pos_order_line_product_attribute_value_rel_order_line_id_fkey"),
            inverseJoinColumns = @JoinColumn(name = "product_attribute_value_id"),
            inverseForeignKey = @ForeignKey(name = "pos_order_line_product_attribute_value_rel_product_attribute_value_id_fkey")
    )
    private Set<ProductAttributeValue> attributeValues = new HashSet<>();*/


    @OneToMany(mappedBy = "line")
    private Set<PosOrderLineProductAttributeValueRel> attributeValues = new HashSet<>();

}
