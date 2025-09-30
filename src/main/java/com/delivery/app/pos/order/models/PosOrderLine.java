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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private PosOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_tmpl_id", referencedColumnName = "id")
    private ProductTemplate productTemplate;

    @Column(name = "qty")
    private Integer quantity;

    @Column(name = "price_unit")
    private Double priceUnit;

    @Column(name = "price_total")
    private Double priceTotal;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PosOrderLineProductAttributeValueRel> attributeValues = new HashSet<>();

    // Helper
    public void addAttributeValue(PosOrderLineProductAttributeValueRel attr) {

        if(attributeValues == null) {
            attributeValues = new HashSet<>();
        }

        attributeValues.add(attr);
        attr.setLine(this);
    }
}
