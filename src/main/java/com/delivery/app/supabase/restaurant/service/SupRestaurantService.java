package com.delivery.app.supabase.restaurant.service;

import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import com.delivery.app.supabase.restaurant.dto.SupRestaurantDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class SupRestaurantService {

    private final RestTemplate restTemplate;

    @Value("${delicias.supabase.url}")
    private String supabaseUrl;

    @Value("${delicias.supabase.key}")
    private String supabaseKey;

    public SupRestaurantService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public void saveRestaurant(RestaurantTemplate tmpl, String defaultLogo) {
        String url = supabaseUrl + "/rest/v1/restaurants";

        SupRestaurantDTO req = createPayload(tmpl, defaultLogo);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);

        HttpEntity<SupRestaurantDTO> request = new HttpEntity<>(req, headers);

        restTemplate.postForEntity(url, request, String.class);

    }

    public void updateRestaurant(Integer id, RestaurantTemplate tmpl, String defaultLogo) {

        String url = supabaseUrl + "/rest/v1/restaurants?id=eq." + id;

        SupRestaurantDTO req = createPayload(tmpl, defaultLogo);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);

        HttpEntity<SupRestaurantDTO> request = new HttpEntity<>(req, headers);

        restTemplate.exchange(
                url,
                HttpMethod.PUT,
                request,
                String.class
        );

    }


    private SupRestaurantDTO createPayload(RestaurantTemplate template, String defaultLogo) {
        return SupRestaurantDTO.builder()
                .id(template.getId())
                .name(template.getName())
                .logoUrl(Optional.ofNullable(template.getImageLogo()).orElse(defaultLogo))
                .address(Optional.ofNullable(template.getAddress()).orElse(""))
                .rating(5)
                .build();
    }
}
