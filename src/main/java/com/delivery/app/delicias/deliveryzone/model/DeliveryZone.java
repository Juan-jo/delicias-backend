package com.delivery.app.delicias.deliveryzone.model;

import com.delivery.app.configs.auditable.model.AuditableEntity;
import com.delivery.app.delicias.deliveryzone.dto.DeliveryZoneDTO;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

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

    @Column(name = "position", columnDefinition = "GEOGRAPHY(Point, 4326)")
    private Point position;

    @Column(name = "radio_position") // En metros
    private Integer radioPosition;

    public void update(DeliveryZoneDTO req) {

        name = req.name();
        hasMinimumAmount = req.hasMinimumAmount();
        minimumAmount = req.minimumAmount();
        active = req.active();
        position = new GeometryFactory().createPoint(new Coordinate(
                req.position().longitude(),
                req.position().latitude()
        ));
        radioPosition = req.position().radioPosition();
    }

}

