package com.delivery.app.product.template.models;

import com.delivery.app.configs.auditable.model.AuditableEntity;
import com.delivery.app.product.attribute.models.ProductAttributeProductTemplateRel;
import com.delivery.app.product.attribute.models.ProductAttributeValue;
import com.delivery.app.product.category.model.ProductCategory;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product_template")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductTemplate extends AuditableEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_template_id_seq")
    @SequenceGenerator(
            name = "product_template_id_seq",
            allocationSize = 1
    )
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "categ_id", referencedColumnName = "id")
    private ProductCategory category;

    // TODO: Add restaurant relation
    private Integer restaurantId;

    private String name;

    private String description;

    @Column(name = "has_configurable_attributes")
    private Boolean hasConfigurableAttributes;

    @Column(name = "list_price")
    private Double listPrice;

    @Column(name = "sales_ok")
    private Boolean salesOK;

    private Boolean active;

    @OneToMany(mappedBy = "id.productTemplate")
    private Set<ProductAttributeProductTemplateRel> attributeProductTemplateRels;

    @OrderBy("sequence asc")
    @ManyToMany
    @JoinTable(
            name = "product_template_product_attribute_value_rel",
            joinColumns = @JoinColumn(name = "product_tmpl_id"),
            foreignKey = @ForeignKey(name = "product_template_product_attribute_value_rel_product_tmpl_id_fkey"),
            inverseJoinColumns = @JoinColumn(name = "product_attribute_value_id"),
            inverseForeignKey = @ForeignKey(name = "product_template_product_attribute_value_rel_product_attribute_value_id_fkey")
    )
    private Set<ProductAttributeValue> attributeValues = new HashSet<>();

    public ProductTemplate(Integer id) {
        this.id = id;
    }
}
