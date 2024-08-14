package com.delivery.app.restaurant.config.service;


import com.delivery.app.configs.DeliciasAppProperties;
import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.product.template.repositories.ProductTemplateRepository;
import com.delivery.app.restaurant.config.dto.RestaurantTmplConfigDTO;
import com.delivery.app.restaurant.config.dto.RestaurantTmplLocationUpdateDTO;
import com.delivery.app.restaurant.menu.model.RestaurantTmplMenu;
import com.delivery.app.restaurant.menu.repository.RestaurantTmplMenuRepository;
import com.delivery.app.restaurant.schedule.model.RestaurantTmplSchedule;
import com.delivery.app.restaurant.schedule.repository.RestaurantTmplScheduleRepository;
import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import com.delivery.app.restaurant.template.repository.RestaurantTemplateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
public class RestaurantTmplConfigService {


    private final RestaurantTemplateRepository restaurantTemplateRepository;
    private final RestaurantTmplScheduleRepository restaurantTmplScheduleRepository;
    private final ProductTemplateRepository productTemplateRepository;
    private final RestaurantTmplMenuRepository restaurantTmplMenuRepository;
    private final DeliciasAppProperties deliciasAppProperties;

    public RestaurantTmplConfigDTO loadConfig(Integer restaurantId) {

        RestaurantTemplate restaurantTmpl = restaurantTemplateRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("restaurantTmpl", "id", restaurantId));

        AtomicReference<Double> lat = new AtomicReference<>();
        AtomicReference<Double> lng = new AtomicReference<>();

        Optional.ofNullable(restaurantTmpl.getPosition()).ifPresent(value -> {

            lat.set(value.getY());
            lng.set(value.getX());

        });

        RestaurantTmplConfigDTO.RestaurantTmplConfigDTOBuilder configDTOBuilder = RestaurantTmplConfigDTO.builder()
                .restaurantId(restaurantTmpl.getId())
                .restaurantName(restaurantTmpl.getName())
                .imageCover(Optional.ofNullable(restaurantTmpl.getImageCover())
                        .map(c->String.format("%s/%s", deliciasAppProperties.getFiles().getResources(), c))
                        .orElse(null))
                .imageLogo(Optional.ofNullable(restaurantTmpl.getImageLogo())
                        .map(c->String.format("%s/%s", deliciasAppProperties.getFiles().getResources(), c))
                        .orElse(null))
                .address(restaurantTmpl.getAddress())
                .latitude(lat.get())
                .longitude(lng.get())
                .recommended(Optional.ofNullable(restaurantTmpl.getRecommendedProductsTmpl())
                        .orElse(List.of()))
                .menus(Optional.ofNullable(restaurantTmpl.getMenus())
                        .map(m -> m.stream().map(r -> RestaurantTmplConfigDTO.Menu.builder()
                                .id(r.getId())
                                .name(r.getName())
                                .available(r.isAvailable())
                                .sequence(r.getSequence())
                                .productsTmpl(r.getProductsTmpl())
                                .build()).toList())
                        .orElse(List.of())

                );

        if (Optional.ofNullable(restaurantTmpl.getSchedules()).orElse(Set.of()).isEmpty()) {
            configDTOBuilder.schedules(generateSchedule(restaurantTmpl));
        } else {
            configDTOBuilder.schedules(
                    restaurantTmpl.getSchedules().stream().map(s -> RestaurantTmplConfigDTO.Schedule.builder()
                            .id(s.getId())
                            .startTime(s.getStartTime())
                            .endTime(s.getEndTime())
                            .dayOfWeek(s.getDayOfWeek())
                            .build()).toList()
            );
        }

        Map<String, List<RestaurantTmplConfigDTO.ProductTmpl>> map = productTemplateRepository.findByRestaurantTmplId(restaurantId)
                .stream()
                .map(r -> RestaurantTmplConfigDTO.ProductTmpl.builder()
                        .id(r.getId())
                        .categ(r.getCategory().getName())
                        .name(r.getName())
                        .build())
                .collect(Collectors.groupingBy(RestaurantTmplConfigDTO.ProductTmpl::categ));

        configDTOBuilder.products(map);

        return configDTOBuilder.build();
    }


    @Transactional
    public void update(RestaurantTmplConfigDTO tmplConfigDTO) {

        RestaurantTemplate restaurantTmpl = restaurantTemplateRepository.findById(tmplConfigDTO.restaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("restaurantTmpl", "id", tmplConfigDTO.restaurantId()));

        restaurantTmpl.setRecommendedProductsTmpl(tmplConfigDTO.recommended());
        restaurantTmpl.setAddress(tmplConfigDTO.address());

        restaurantTmplScheduleRepository.saveAll(
                tmplConfigDTO.schedules().stream()
                        .map(s -> RestaurantTmplSchedule.builder()
                                .id(s.id())
                                .startTime(s.startTime())
                                .endTime(s.endTime())
                                .dayOfWeek(s.dayOfWeek())
                                .restaurantTmpl(new RestaurantTemplate(tmplConfigDTO.restaurantId()))
                                .available(s.available())
                                .build()).toList()
        );

        restaurantTmplMenuRepository.saveAll(
                tmplConfigDTO.menus().stream()
                        .map(m -> RestaurantTmplMenu.builder()
                                .id(m.id())
                                .name(m.name())
                                .restaurantTmpl(new RestaurantTemplate(tmplConfigDTO.restaurantId()))
                                .available(m.available())
                                .sequence(m.sequence())
                                .productsTmpl(m.productsTmpl())
                                .build()).toList()
        );

    }

    @Transactional
    public void updateLocation(RestaurantTmplLocationUpdateDTO updateDTO) {

        RestaurantTemplate restaurantTmpl = restaurantTemplateRepository.findById(updateDTO.restaurantTmplId())
                .orElseThrow(() -> new ResourceNotFoundException("restaurantTmpl", "id", updateDTO.restaurantTmplId()));


        restaurantTmpl.updatePosition(updateDTO.longitude(), updateDTO.latitude());
    }

    private List<RestaurantTmplConfigDTO.Schedule> generateSchedule(RestaurantTemplate restaurantTmpl) {

        return restaurantTmplScheduleRepository.saveAll(
                Arrays.stream(DayOfWeek.values()).map(day -> RestaurantTmplSchedule.builder()
                        .restaurantTmpl(restaurantTmpl)
                        .dayOfWeek(day)
                        .startTime(LocalTime.parse("00:00"))
                        .endTime(LocalTime.parse("00:00"))
                        .build()).toList()
        ).stream().map(s -> RestaurantTmplConfigDTO.Schedule.builder()
                .id(s.getId())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .dayOfWeek(s.getDayOfWeek())
                .build()).toList();
    }

}
