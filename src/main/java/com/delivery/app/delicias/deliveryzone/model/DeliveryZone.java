package com.delivery.app.delicias.deliveryzone.model;

import com.delivery.app.configs.auditable.model.AuditableEntity;
import com.delivery.app.delicias.deliveryzone.dto.DeliveryZoneDTO;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

@Entity
@Table(name = "mobile_delivery_zone")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryZone extends AuditableEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "mobile_zone_id_seq")
    @SequenceGenerator(
            name = "mobile_zone_id_seq",
            allocationSize = 1
    )
    private Integer id;

    private String name;

    @Column(name = "has_minimum_amount")
    private boolean hasMinimumAmount;

    @Column(name = "minimum_amount")
    private Integer minimumAmount;

    private boolean active;

    @Column(columnDefinition = "geometry(POLYGON,4326)")
    private Polygon area;

    public void update(DeliveryZoneDTO req, Polygon area) {

        name = req.name();
        hasMinimumAmount = req.hasMinimumAmount();
        minimumAmount = req.minimumAmount();
        active = req.active();
        this.area = area;
    }

}

