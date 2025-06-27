package com.delivery.app.product.attribute.models;

import com.delivery.app.configs.auditable.model.AuditableEntity;
import com.delivery.app.product.template.dtos.ProductTemplateAttributeDTO;
import com.delivery.app.product.template.models.ProductTemplate;
import com.delivery.app.restaurant.template.model.RestaurantTemplate;
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

    private Integer sequence;

    // TODO Verify future delete rel
    /*@ManyToOne
    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    private RestaurantTemplate restaurantTmpl;*/

    @ManyToOne
    @JoinColumn(name = "product_tmpl_id", referencedColumnName = "id")
    private ProductTemplate productTemplate;

    public ProductAttribute(Integer id) {
        this.id = id;
    }

    public void update(ProductTemplateAttributeDTO attributeDTO) {
        this.name = attributeDTO.name();
        this.displayType = attributeDTO.displayType();
        this.sequence = attributeDTO.sequence();
    }
}
