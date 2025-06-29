package com.delivery.app.delicias.deliveryzone.service;

import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.delicias.deliveryzone.dto.DeliveryZoneDTO;
import com.delivery.app.delicias.deliveryzone.dto.DeliveryZoneReqFilter;
import com.delivery.app.delicias.deliveryzone.model.DeliveryZone;
import com.delivery.app.delicias.deliveryzone.repository.DeliveryZoneRepository;
import lombok.AllArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@AllArgsConstructor
@Service
public class DeliveryZoneService {


    private final DeliveryZoneRepository deliveryZoneRepository;

    public Page<DeliveryZoneDTO> filter(DeliveryZoneReqFilter filter) {

        return deliveryZoneRepository.findByFilter(filter.getName(), filter.pageable())
                .map(modelToDTO());
    }

    @Transactional
    public DeliveryZoneDTO create(DeliveryZoneDTO req) {

        DeliveryZone entity = DeliveryZone.builder()
                .name(req.name())
                .hasMinimumAmount(req.hasMinimumAmount())
                .minimumAmount(req.minimumAmount())
                .active(req.active())
                .position(new GeometryFactory().createPoint(new Coordinate(
                        req.position().longitude(),
                        req.position().latitude()
                )))
                .radioPosition(req.position().radioPosition())
                .build();

        DeliveryZone saved = deliveryZoneRepository.save(entity);

        return modelToDTO().apply(saved);
    }

    @Transactional
    public DeliveryZoneDTO update(DeliveryZoneDTO req) {

        DeliveryZone entity = deliveryZoneRepository.findById(req.id())
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryZone", "id", req.id()));
        entity.update(req);


        deliveryZoneRepository.save(entity);

        return modelToDTO().apply(entity);
    }


    public DeliveryZoneDTO findById(Integer deliveryZoneId) {

        return deliveryZoneRepository.findById(deliveryZoneId)
                .map(modelToDTO())
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryZone", "id", deliveryZoneId));

    }

    @Transactional
    public void deleteById(Integer deliveryZoneId) {

        DeliveryZone deliveryZone = deliveryZoneRepository.findById(deliveryZoneId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryZone", "id", deliveryZoneId));

        deliveryZoneRepository.delete(deliveryZone);

    }


    private Function<DeliveryZone, DeliveryZoneDTO> modelToDTO() {
        return model -> DeliveryZoneDTO.builder()
                .id(model.getId())
                .name(model.getName())
                .hasMinimumAmount(model.isHasMinimumAmount())
                .minimumAmount(model.getMinimumAmount())
                .active(model.isActive())
                .position(DeliveryZoneDTO.Position.builder()
                        .latitude(model.getPosition().getY())
                        .longitude(model.getPosition().getX())
                        .radioPosition(model.getRadioPosition())
                        .build())
                .build();
    }
}
