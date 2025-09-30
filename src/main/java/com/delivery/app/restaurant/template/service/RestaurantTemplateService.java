package com.delivery.app.restaurant.template.service;

import com.delivery.app.configs.DeliciasAppProperties;
import com.delivery.app.configs.exception.common.ResourceNotFoundException;
import com.delivery.app.restaurant.template.dto.*;
import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import com.delivery.app.restaurant.template.repository.RestaurantTemplateRepository;
import com.delivery.app.supabase.restaurant.service.SupRestaurantService;
import com.delivery.app.supabase.storage.SupabaseStorageService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class RestaurantTemplateService {

    private final RestaurantTemplateRepository restaurantTemplateRepository;

    private final DeliciasAppProperties deliciasAppProperties;

    private final SupRestaurantService supRestaurantService;
    private final SupabaseStorageService supabaseStorageService;

    @Transactional
    public RestaurantTemplateDTO create(RestaurantTemplateDTO templateDTO) {

        RestaurantTemplate restaurantTemplate = RestaurantTemplate.builder()
                .name(templateDTO.name())
                .description(templateDTO.description())
                .phone(templateDTO.phone())
                .build();

        restaurantTemplateRepository.save(restaurantTemplate);

        supRestaurantService.saveRestaurant(restaurantTemplate, deliciasAppProperties.getSupabase().getLogo());

        return modelToRestaurantTemplateDTO(restaurantTemplate);
    }

    @Transactional
    public RestaurantTemplateDTO update(RestaurantTemplateDTO templateDTO) {

        RestaurantTemplate restaurantTemplate = restaurantTemplateRepository.findById(templateDTO.id())
                .orElseThrow(() -> new ResourceNotFoundException("restaurantTpml", "id", templateDTO.id()));

        restaurantTemplate.update(templateDTO);

        supRestaurantService.updateRestaurant(
                templateDTO.id(), restaurantTemplate, deliciasAppProperties.getSupabase().getLogo()
        );

        return modelToRestaurantTemplateDTO(restaurantTemplate);
    }

    @Transactional(readOnly = true)
    public RestaurantTemplateDTO findById(Integer id) {

        return restaurantTemplateRepository.findById(id)
                .map(this::modelToRestaurantTemplateDTO)
                .orElseThrow(() -> new ResourceNotFoundException("restaurantTpml", "id", id));

    }

    @Transactional(readOnly = true)
    public Page<RestaurantTemplateRow> findByFilter(
            RestaurantTemplateReqFilterRows reqFilterRows
    ) {

        return restaurantTemplateRepository.findByFilter(
                StringUtils.upperCase(reqFilterRows.getName()),
                reqFilterRows.pageable()
        ).map(r -> RestaurantTemplateRow.builder()
                .id(r.getId())
                .name(r.getName())
                .enabledMobile(r.isEnabledMobile())
                .updatedAt(r.getUpdatedAt())
                .createdAt(r.getCreatedAt())
                .picture(
                        Optional.ofNullable(r.getImageLogo())
                                .orElse(deliciasAppProperties.getSupabase().getLogo())
                )
                .build());

    }

    @Transactional
    public void deleteById(Integer tmplId) {

        RestaurantTemplate template = restaurantTemplateRepository.findById(tmplId)
                .orElseThrow(() -> new ResourceNotFoundException("tmpl", "id", tmplId));

        restaurantTemplateRepository.delete(template);
    }

    @Transactional(readOnly = true)
    public List<RestaurantTmplOptionDTO> tmplOptionDTOList() {

        return restaurantTemplateRepository.findAll()
                .stream().map(r -> RestaurantTmplOptionDTO.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .picture(
                                Optional.ofNullable(r.getImageLogo())
                                        .orElse(deliciasAppProperties.getSupabase().getLogo())
                        )
                        .build())
                .toList();
    }

    @Transactional
    public Map<String, String> uploadCover(Integer restaurantTmplId, MultipartFile cover) throws IOException {

        RestaurantTemplate restaurantTemplate = restaurantTemplateRepository.findById(restaurantTmplId)
                .orElseThrow(() -> new ResourceNotFoundException("restaurantTpml", "id", restaurantTmplId));

        String fileName = supabaseStorageService.uploadImage(cover);

        restaurantTemplate.setImageCover(fileName);

        return Map.of("picture", fileName);
    }

    @Transactional
    public Map<String, String> uploadLogo(Integer restaurantTmplId, MultipartFile logoFile) throws IOException {

        RestaurantTemplate restaurantTemplate = restaurantTemplateRepository.findById(restaurantTmplId)
                .orElseThrow(() -> new ResourceNotFoundException("restaurantTpml", "id", restaurantTmplId));

        String fileName = supabaseStorageService.uploadImage(logoFile);

        restaurantTemplate.setImageLogo(fileName);

        supRestaurantService.updateRestaurant(
                restaurantTmplId, restaurantTemplate, deliciasAppProperties.getSupabase().getLogo()
        );

        return Map.of("picture", fileName);
    }

    private RestaurantTemplateDTO modelToRestaurantTemplateDTO(RestaurantTemplate template) {

        return RestaurantTemplateDTO.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .phone(template.getPhone())
                .logoPicture(
                        Optional.ofNullable(template.getImageLogo())
                                .orElse(deliciasAppProperties.getSupabase().getLogo())
                )
                .build();

    }
}
