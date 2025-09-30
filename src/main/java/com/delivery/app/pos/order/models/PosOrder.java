package com.delivery.app.pos.order.models;

import com.delivery.app.configs.auditable.model.AuditableEntity;
import com.delivery.app.pos.enums.OrderStatus;
import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import com.delivery.app.security.model.UserAddress;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import com.vladmihalcea.hibernate.type.json.JsonType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    private RestaurantTemplate restaurantTmpl;

    @Column(name = "user_uid")
    private UUID userUID;

    @Column(name = "delivery_uid")
    private UUID deliveryUserUID;

    @Column(name = "date_order")
    private LocalDate dateOrder;

    @OrderBy("id asc")
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PosOrderLine> lines = new HashSet<>();;


    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Column(name = "delivery_order_rel_id")
    private Integer deliveryOrderRelId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<PosOrderAdjustment> adjustments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_address_id", referencedColumnName = "id")
    private UserAddress userAddress;

    public void addLine(PosOrderLine line) {
        if(lines == null) {
            lines = new HashSet<>();
        }
        lines.add(line);
        line.setOrder(this);
    }

}
