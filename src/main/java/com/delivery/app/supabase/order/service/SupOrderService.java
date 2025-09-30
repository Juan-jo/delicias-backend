package com.delivery.app.supabase.order.service;

import com.delivery.app.pos.order.models.PosOrder;
import com.delivery.app.pos.order.models.PosOrderLine;
import com.delivery.app.supabase.order.dtos.SupOrderChangeStatusReqDTO;
import com.delivery.app.supabase.order.dtos.SupOrderDTO;
import com.delivery.app.supabase.order.dtos.SupOrderLineDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SupOrderService {

    private final RestTemplate restTemplate;


    @Value("${delicias.supabase.url}")
    private String supabaseUrl;

    @Value("${delicias.supabase.key}")
    private String supabaseKey;

    private final String URL_ORDERS = "/rest/v1/orders";
    private final String URL_ORDER_LINE = "/rest/v1/order_line";

    public SupOrderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void createOrder(PosOrder order) {

        saveOrder(order);

        saveLines(order.getLines());

    }

    public void changeStatus(SupOrderChangeStatusReqDTO req) {

        String url = supabaseUrl + URL_ORDERS + "?id=eq." + req.orderId();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, String> requestBody = Collections.singletonMap("status", req.status());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.PATCH,
                entity,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.NO_CONTENT || response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Status actualizado correctamente para la orden " + req.orderId());
        } else {
            throw new RuntimeException("Error actualizando el status. Código HTTP: " + response.getStatusCode());
        }

    }


    private void saveOrder(PosOrder order) {

        String url = supabaseUrl + URL_ORDERS;

        SupOrderDTO req = createOrderPayload(order);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);

        HttpEntity<SupOrderDTO> request = new HttpEntity<>(req, headers);

        restTemplate.postForEntity(url, request, String.class);

    }


    private void saveLines(Set<PosOrderLine> lines) {

        String url = supabaseUrl + URL_ORDER_LINE;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);

        for (PosOrderLine line: lines) {

            SupOrderLineDTO req = createLinePayload(line);
            HttpEntity<SupOrderLineDTO> request = new HttpEntity<>(req, headers);

            restTemplate.postForEntity(url, request, String.class);
        }
    }

    private SupOrderLineDTO createLinePayload(PosOrderLine line) {

        String desc = "";

        if(line.getAttributeValues()!= null) {

            desc = line.getAttributeValues().stream()
                    .map(
                            r -> String.format("%s: %s, \n", r.getAttributeValue().getAttribute().getName(), r.getAttributeValue().getName()))
                    .collect(Collectors.joining(". \n"));
        }


        return SupOrderLineDTO.builder()
                .id(line.getId())
                .productName(line.getProductTemplate().getName())
                .quantity(line.getQuantity())
                .price(line.getPriceTotal())
                .description(desc)
                .orderId(line.getOrder().getId())
                .build();
    }

    private SupOrderDTO createOrderPayload(PosOrder order) {
        return SupOrderDTO.builder()
                .id(order.getId())
                .userId(order.getUserUID())
                .status(order.getStatus().name())
                .restaurantId(order.getRestaurantTmpl().getId())
                .build();
    }

}
