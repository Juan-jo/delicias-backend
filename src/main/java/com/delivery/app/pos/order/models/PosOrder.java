package com.delivery.app.pos.order.models;

import com.delivery.app.configs.auditable.model.AuditableEntity;
import com.delivery.app.pos.enums.OrderStatus;
import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "pos_order")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PosOrder extends AuditableEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "pos_order_id_seq")
    @SequenceGenerator(
            name = "pos_order_id_seq",
            allocationSize = 1
    )
    private Integer id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String notes;

    @Column(name = "amount_total")
    private Double amountTotal;

    @Column(name = "amount_subtotal")
    private Double amountSubtotal;

    @Column(name = "amount_discount")
    private Double amountDiscount;

    @Column(name = "cost_service")
    private Double costService;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    private RestaurantTemplate restaurantTmpl;

    @Column(name = "user_uid")
    private UUID userUID;

    @Column(name = "delivery_uid")
    private UUID deliveryUserUID;

    @Column(name = "date_order")
    private LocalDate dateOrder;

    @OrderBy("id asc")
    @OneToMany(mappedBy = "order")
    private Set<PosOrderLine> lines;

    @Column(name = "keycloak_user_id")
    private UUID userId;

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Column(name = "delivery_order_rel_id")
    private Integer deliveryOrderRelId;
}
