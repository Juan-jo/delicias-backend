package com.delivery.app.pos.kanban.model;

import com.delivery.app.pos.order.models.PosOrder;
import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pos_restaurant_kanban")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PosRestaurantKanban {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "pos_restaurant_kanban_id_seq")
    @SequenceGenerator(
            name = "pos_restaurant_kanban_id_seq",
            allocationSize = 1
    )
    private Integer id;



    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    PosOrder order;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    private RestaurantTemplate restaurantTmpl;

}
