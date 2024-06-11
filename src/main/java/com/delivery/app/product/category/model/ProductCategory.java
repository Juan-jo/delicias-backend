package com.delivery.app.product.category.model;

import com.delivery.app.configs.auditable.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_category")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategory extends AuditableEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_category_id_seq")
    @SequenceGenerator(
            name = "product_category_id_seq",
            allocationSize = 1
    )
    private Integer id;

    private String name;

    private String completeName;

    private String parentPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private ProductCategory parent;


    public ProductCategory(Integer id) {
        this.id = id;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
}
