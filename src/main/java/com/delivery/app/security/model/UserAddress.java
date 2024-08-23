package com.delivery.app.security.model;

import com.delivery.app.security.enums.UserAddressType;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.util.UUID;

@Entity
@Table(name = "user_addresses")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAddress {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_address_id_seq")
    @SequenceGenerator(
            name = "user_address_id_seq",
            allocationSize = 1
    )
    private Integer id;

    @Column(name = "keycloak_user_id")
    private UUID keycloakUserId;


    @Column(name = "position", columnDefinition = "GEOGRAPHY(Point, 4326)")
    private Point position;

    @Column(name = "type_address")
    @Enumerated(EnumType.STRING)
    private UserAddressType addressType;

    private String details;

    @Column(name = "company_name")
    private String companyName;

    private String street;

    private String address;

    private String indications;

}
