package com.delivery.app.restaurant.template.model;

import com.delivery.app.configs.auditable.model.AuditableEntity;
import com.delivery.app.restaurant.menu.model.RestaurantTmplMenu;
import com.delivery.app.restaurant.schedule.model.RestaurantTmplSchedule;
import com.delivery.app.restaurant.template.dto.RestaurantTemplateDTO;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "restaurant_template")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantTemplate extends AuditableEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "restaurant_template_id_seq")
    @SequenceGenerator(
            name = "restaurant_template_id_seq",
            allocationSize = 1
    )
    private Integer id;

    private String name;

    @Column(columnDefinition = "text")
    private String description;

    private String phone;

    @Column(name = "position", columnDefinition = "GEOGRAPHY(Point, 4326)")
    private Point position;

    @Column(name = "address")
    private String address;

    @Column(name = "image_cover")
    private String imageCover;

    @Column(name = "image_logo")
    private String imageLogo;

    @OneToMany(mappedBy = "restaurantTmpl")
    @OrderBy("id")
    private Set<RestaurantTmplSchedule> schedules;

    @OneToMany(mappedBy = "restaurantTmpl")
    @OrderBy("sequence")
    private Set<RestaurantTmplMenu> menus;

    @Column(columnDefinition = "int[]", name = "recommended_products_tmpl")
    private List<Integer> recommendedProductsTmpl;

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void update(RestaurantTemplateDTO templateDTO) {
        this.name = templateDTO.name();
        this.description = templateDTO.description();
        this.phone = templateDTO.phone();
    }

    public void updatePosition(double longitude, double latitude) {
        GeometryFactory geometryFactory = new GeometryFactory();
        this.position = geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

    public RestaurantTemplate(Integer id) {
        this.id = id;
    }
}
