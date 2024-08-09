package com.delivery.app.restaurant.menu.model;

import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "restaurant_menu")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantTmplMenu {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "restaurant_menu_seq")
    @SequenceGenerator(
            name = "restaurant_menu_seq",
            allocationSize = 1
    )
    private Integer id;

    @Column(columnDefinition = "citext")
    private String name;

    @ManyToOne
    @JoinColumn(name="restaurant_id", referencedColumnName = "id")
    private RestaurantTemplate restaurantTmpl;

    @Column(columnDefinition = "int[]")
    private List<Integer> productsTmpl;

    private boolean available;

}
