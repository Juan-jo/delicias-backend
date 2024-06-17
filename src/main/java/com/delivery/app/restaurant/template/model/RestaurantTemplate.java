package com.delivery.app.restaurant.template.model;

import com.delivery.app.configs.auditable.model.AuditableEntity;
import com.delivery.app.restaurant.template.dto.RestaurantTemplateDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    public RestaurantTemplate(Integer id) {
        this.id = id;
    }
}
