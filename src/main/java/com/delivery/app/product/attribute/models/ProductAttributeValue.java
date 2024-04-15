package com.delivery.app.product.attribute.models;

import com.delivery.app.configs.auditable.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_attribute_value")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductAttributeValue extends AuditableEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_attribute_value_id_seq")
    @SequenceGenerator(
            name = "product_attribute_value_id_seq",
            allocationSize = 1
    )
    private Integer id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", referencedColumnName = "id")
    private ProductAttribute attribute;

    @Column(name = "default_extra_price")
    private Double extraPrice;

    private Integer sequence;

    public ProductAttributeValue(Integer id) {
        this.id = id;
    }
}
