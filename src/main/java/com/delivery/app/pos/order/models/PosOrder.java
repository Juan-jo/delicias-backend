package com.delivery.app.pos.order.models;

import com.delivery.app.configs.auditable.model.AuditableEntity;
import com.delivery.app.pos.status.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private Integer restaurantId;

    @Column(name = "user_uid")
    private UUID userUID;

    @Column(name = "delivery_uid")
    private UUID deliveryUserUID;

    @Column(name = "date_order")
    private LocalDate dateOrder;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
