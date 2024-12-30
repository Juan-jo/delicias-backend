package com.delivery.app.mobile.user.models;

import com.delivery.app.product.template.models.ProductTemplate;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "shopping_cart_line")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartLine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "shopping_cart_id", referencedColumnName = "id")
    private ShoppingCart shoppingCart;

    @ManyToOne
    @JoinColumn(name = "product_tmpl_id", referencedColumnName = "id")
    private ProductTemplate productTemplate;

    @Column(name = "qty")
    private Integer qty;

    @Column(columnDefinition = "int[]", name = "attr_value_ids")
    private Set<Integer> attrValuesIds;

}
