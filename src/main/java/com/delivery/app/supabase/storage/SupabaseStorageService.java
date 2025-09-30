package com.delivery.app.supabase.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    @Value("${delicias.supabase.domain}")
    private String domain;

    @Value("${delicias.supabase.url}")
    private String supabaseUrl;

    @Value("${delicias.supabase.key}")
    private String supabaseKey;

    @Value("${delicias.supabase.bucket}")
    private String bucketName;

    private final RestTemplate restTemplate;

    public SupabaseStorageService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String uploadImage(MultipartFile file) throws IOException {

        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        String endpoint = String.format("%s/storage/v1/object/%s/%s",
                supabaseUrl, bucketName, fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(file.getContentType())); // tipo dinámico
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.set("apikey", supabaseKey);

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                endpoint,
                HttpMethod.PUT,
                requestEntity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return String.format("%s/storage/v1/object/public/%s/%s",
                    domain, bucketName, fileName);
        } else {
            throw new RuntimeException("Error al subir imagen: " + response.getBody());
        }
    }

}
