package com.delivery.app.mobile.service;

import com.delivery.app.configs.DeliciasAppProperties;
import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.mobile.dtos.MobileGeocodingDTO;
import com.delivery.app.mobile.dtos.MobileUserAddressDTO;
import com.delivery.app.security.dtos.UserAddressDTO;
import com.delivery.app.security.model.UserAddress;
import com.delivery.app.security.repository.UserAddressRepository;
import com.delivery.app.security.services.AuthenticationFacade;
import com.delivery.app.utils.GoogleMapsService;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import lombok.AllArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class MobileUserService {

    private final GoogleMapsService googleMapsService;
    private final DeliciasAppProperties deliciasAppProperties;
    private final UserAddressRepository userAddressRepository;
    private final AuthenticationFacade authenticationFacade;


    public Set<MobileGeocodingDTO> searchNearby(double lat, double lng) throws IOException, InterruptedException, ApiException {

        List<UserAddress> addresses = userAddressRepository.findByKeycloakUserId(authenticationFacade.userId());

        /*if(!addresses.isEmpty()) {
            return Set.of();
        }*/

        if(deliciasAppProperties.getProduction()) {
            return Arrays.stream(googleMapsService.reverseGeocode(lat, lng)).map(
                    geoItem -> {

                        String address = "";
                        String ruta = "";

                        for (AddressComponent component : geoItem.addressComponents) {

                            if (containsType(component.types, AddressComponentType.ROUTE)) {
                                ruta = component.longName;
                            }

                            // Buscar el vecindario (neighborhood o sublocality)
                            if (containsType(component.types, AddressComponentType.NEIGHBORHOOD) ||
                                    containsType(component.types, AddressComponentType.SUBLOCALITY)) {

                                address += component.longName;

                            }
                            // Ciudad
                            if (containsType(component.types, AddressComponentType.LOCALITY)) {


                                if(address.isEmpty()) {
                                    address += component.longName;
                                }
                                else {
                                    address += ", " + component.longName;
                                }
                            }
                            // Buscar el país (country)
                            if (containsType(component.types, AddressComponentType.COUNTRY)) {

                                if(address.isEmpty()) {
                                    address += component.longName;
                                }
                                else {
                                    address += ", " + component.longName;
                                }
                            }

                        }

                        return MobileGeocodingDTO.builder()
                                .street(ruta)
                                .address(address)
                                .latitude(lat)
                                .longitude(lng)
                                .build();
                    }
            ).collect(Collectors.toSet());

        }
        else {
            // mock develop
            return Set.of(
                    MobileGeocodingDTO.builder()
                            .street("Via sin nombre")
                            .address("Zocuiteco Benito Juarez, Mexico")
                            .latitude(lat)
                            .longitude(lng)
                            .build()
            );
        }
    }


    @Transactional
    public UserAddressDTO addAddress(MobileUserAddressDTO dto) {

        GeometryFactory geometryFactory = new GeometryFactory();

        UserAddress userAddress = userAddressRepository.save(UserAddress.builder()
                        .keycloakUserId(authenticationFacade.userId())
                        .addressType(dto.addressType())
                        .indications(dto.indications())
                        .street(dto.street())
                        .address(dto.address())
                        .position(geometryFactory.createPoint(new Coordinate(dto.longitude(), dto.latitude())))
                        .details(switch (dto.addressType()) {
                            case HOME, DEPTO, OTHER -> dto.details();
                            case OFFICE -> "";
                        })
                        .companyName(switch (dto.addressType()) {
                            case HOME, DEPTO, OTHER -> "";
                            case OFFICE -> dto.companyName();
                        })
                .build());

        return modelToUserAddressDTO(userAddress);
    }

    @Transactional
    public UserAddressDTO update(MobileUserAddressDTO dto) {

        UserAddress userAddress = userAddressRepository.findById(dto.id())
                .orElseThrow(() -> new ResourceNotFoundException("UserAddress", "id", dto.id()));

        userAddress.update(dto);

        userAddressRepository.save(userAddress);

        return modelToUserAddressDTO(userAddress);
    }


    private UserAddressDTO modelToUserAddressDTO(UserAddress userAddress) {
        return UserAddressDTO.builder()
                .id(userAddress.getId())
                .addressType(userAddress.getAddressType())
                .details(userAddress.getDetails())
                .companyName(userAddress.getCompanyName())
                .street(userAddress.getStreet())
                .address(userAddress.getAddress())
                .latitude(userAddress.getPosition().getY())
                .longitude(userAddress.getPosition().getX())
                .indications(userAddress.getIndications())
                .icon(switch (userAddress.getAddressType()) {
                    case HOME -> "assets/fd/home.svg";
                    case DEPTO -> "assets/fd/depto.svg";
                    case OFFICE -> "assets/fd/office.svg";
                    case OTHER -> "assets/fd/other.svg";
                })
                .build();
    }

    private boolean containsType(AddressComponentType[] types, AddressComponentType type) {
        for (AddressComponentType t : types) {
            if (t == type) {
                return true;
            }
        }
        return false;
    }
}
