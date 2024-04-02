package com.delivery.app.product.attribute.models;

import com.delivery.app.configs.auditable.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_attribute")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductAttribute extends AuditableEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_attribute_id_seq")
    @SequenceGenerator(
            name = "product_attribute_id_seq",
            allocationSize = 1
    )
    private Integer id;

    private String name;

    @Column(name = "display_type")
    private String displayType;

    public ProductAttribute(Integer id) {
        this.id = id;
    }

}
