package com.delivery.app.delicias.deliveryzone.service;

import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.delicias.deliveryzone.dto.DeliverZoneLatLngDTO;
import com.delivery.app.delicias.deliveryzone.dto.DeliveryZoneDTO;
import com.delivery.app.delicias.deliveryzone.dto.DeliveryZoneReqFilter;
import com.delivery.app.delicias.deliveryzone.model.DeliveryZone;
import com.delivery.app.delicias.deliveryzone.repository.DeliveryZoneRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class DeliveryZoneService {


    private final DeliveryZoneRepository deliveryZoneRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public Page<DeliveryZoneDTO> filter(DeliveryZoneReqFilter filter) {

        return deliveryZoneRepository.findByFilter(
                        StringUtils.upperCase(filter.getName()),
                        filter.pageable())
                .map(DeliveryZoneService::toDeliveryZoneDTO);
    }

    @Transactional
    public DeliveryZoneDTO create(DeliveryZoneDTO req) {

        DeliveryZone entity = DeliveryZone.builder()
                .name(req.name())
                .hasMinimumAmount(req.hasMinimumAmount())
                .minimumAmount(req.minimumAmount())
                .active(req.active())
                .area(createPolygonFromLatLng(req.coordinates()))
                .build();

        DeliveryZone saved = deliveryZoneRepository.save(entity);

        return toDeliveryZoneDTO(saved);
    }

    @Transactional
    public DeliveryZoneDTO update(DeliveryZoneDTO req) {

        DeliveryZone entity = deliveryZoneRepository.findById(req.id())
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryZone", "id", req.id()));

        entity.update(req, createPolygonFromLatLng(req.coordinates()));

        deliveryZoneRepository.save(entity);

        return toDeliveryZoneDTO(entity);
    }


    public DeliveryZoneDTO findById(Integer deliveryZoneId) {

        return deliveryZoneRepository.findById(deliveryZoneId)
                .map(DeliveryZoneService::toDeliveryZoneDTO)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryZone", "id", deliveryZoneId));

    }

    @Transactional
    public void deleteById(Integer deliveryZoneId) {

        DeliveryZone deliveryZone = deliveryZoneRepository.findById(deliveryZoneId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryZone", "id", deliveryZoneId));

        deliveryZoneRepository.delete(deliveryZone);

    }

    public List<DeliverZoneLatLngDTO> all() {

        return this.deliveryZoneRepository.findAll()
                .stream()
                .map(DeliveryZoneService::toDeliverZoneLatLngDTO)
                .toList();
    }

    public static DeliveryZoneDTO toDeliveryZoneDTO(DeliveryZone model) {

        List<List<Double>> coordinates = getCoordinates(model);

        return DeliveryZoneDTO.builder()
                .id(model.getId())
                .name(model.getName())
                .hasMinimumAmount(model.isHasMinimumAmount())
                .minimumAmount(model.getMinimumAmount())
                .active(model.isActive())
                .coordinates(coordinates)
                .build();
    }

    public static DeliverZoneLatLngDTO toDeliverZoneLatLngDTO(DeliveryZone model) {

        List<List<Double>> coordinates = getCoordinates(model);

        return DeliverZoneLatLngDTO.builder()
                .name(model.getName())
                .active(model.isActive())
                .coordinates(coordinates)
                .build();
    }

    @NotNull
    private static List<List<Double>> getCoordinates(DeliveryZone model) {
        Polygon polygon = model.getArea();
        Coordinate[] coords = polygon.getExteriorRing().getCoordinates();

        List<List<Double>> coordinates = new ArrayList<>();
        for (Coordinate c : coords) {
            List<Double> point = List.of(c.getY(), c.getX()); // [lat, lng]
            coordinates.add(point);
        }
        return coordinates;
    }

    public Polygon createPolygonFromLatLng(List<List<Double>> points) {

        Coordinate[] coords = points.stream()
                .map(p -> new Coordinate(p.get(1), p.get(0))) // (lng, lat)
                .toArray(Coordinate[]::new);

        if (!coords[0].equals2D(coords[coords.length - 1])) {
            Coordinate[] closed = new Coordinate[coords.length + 1];
            System.arraycopy(coords, 0, closed, 0, coords.length);
            closed[closed.length - 1] = coords[0];
            coords = closed;
        }

        LinearRing shell = geometryFactory.createLinearRing(coords);

        return geometryFactory.createPolygon(shell, null);
    }
}
