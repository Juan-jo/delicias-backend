package com.delivery.app.configs.supabase;

import org.apache.http.impl.client.CloseableHttpClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.config.RequestConfig;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SupabaseWebClientConfig {

    @Bean
    public RestTemplate restTemplate() {

        //return new RestTemplate();

        // Configuración de timeout
        RequestConfig requestConfig = RequestConfig.custom()
                .setResponseTimeout(5000, java.util.concurrent.TimeUnit.MILLISECONDS)
                .build();

        HttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();

        // Integrarlo con Spring
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(factory);

    }
}
