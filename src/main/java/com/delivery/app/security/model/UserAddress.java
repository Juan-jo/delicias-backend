package com.delivery.app.security.model;

import com.delivery.app.mobile.dtos.MobileUserAddressDTO;
import com.delivery.app.security.enums.UserAddressType;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
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

    public void update(MobileUserAddressDTO userAddressDTO) {

        this.addressType = userAddressDTO.addressType();
        this.street = userAddressDTO.street();
        this.address = userAddressDTO.address();
        this.indications = userAddressDTO.indications();

        GeometryFactory geometryFactory = new GeometryFactory();
        this.position = geometryFactory.createPoint(
                new Coordinate(userAddressDTO.longitude(), userAddressDTO.latitude()));

        details = switch (addressType) {
            case HOME, DEPTO, OTHER -> userAddressDTO.details();
            case OFFICE -> "";
        };

        companyName = switch (addressType) {
                    case HOME, DEPTO, OTHER -> "";
                    case OFFICE -> userAddressDTO.companyName();
        };
    }

}
