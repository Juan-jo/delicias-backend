package com.delivery.app.mobile.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.util.List;


@Entity
@Table(name = "mobile_config")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MobileConfig {

    @Id
    private Integer id;

    @Column(columnDefinition = "int[]", name = "available_restaurants")
    private List<Integer> availableRestaurants;

    @Column(name = "cost_service")
    private Double costService;

    @Column(name = "zone_center_location", columnDefinition = "GEOGRAPHY(Point, 4326)")
    private Point zone;

}
