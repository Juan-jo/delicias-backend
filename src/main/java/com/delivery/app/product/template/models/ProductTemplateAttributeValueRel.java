package com.delivery.app.product.template.models;

import com.delivery.app.configs.auditable.model.AuditableEntity;
import com.delivery.app.product.attribute.models.ProductAttribute;
import com.delivery.app.product.attribute.models.ProductAttributeValue;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_template_product_attribute_value_rel")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductTemplateAttributeValueRel extends AuditableEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_template_product_attribute_value_rel_id_seq")
    @SequenceGenerator(
            name = "product_template_product_attribute_value_rel_id_seq",
            allocationSize = 1
    )
    private Integer id;

    @OneToOne
    @JoinColumn(name = "product_tmpl_id", referencedColumnName = "id")
    private ProductTemplate template;

    @ManyToOne
    @JoinColumn(name = "product_attribute_value_id", referencedColumnName = "id")
    private ProductAttributeValue attributeValue;

    @ManyToOne
    @JoinColumn(name = "attribute_id", referencedColumnName = "id")
    private ProductAttribute attribute;

    private Boolean active;
}
