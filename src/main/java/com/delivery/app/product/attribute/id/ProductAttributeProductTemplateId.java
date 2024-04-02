package com.delivery.app.product.attribute.id;

import com.delivery.app.product.attribute.models.ProductAttribute;
import com.delivery.app.product.template.models.ProductTemplate;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductAttributeProductTemplateId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_attribute_id", referencedColumnName = "id")
    private ProductAttribute attribute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_template_id", referencedColumnName = "id")
    private ProductTemplate productTemplate;
}
