package com.delivery.app.product.attribute.models;

import com.delivery.app.product.attribute.id.ProductAttributeProductTemplateId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "product_attribute_product_template_rel")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductAttributeProductTemplateRel {

    @EmbeddedId
    private ProductAttributeProductTemplateId id;
}
