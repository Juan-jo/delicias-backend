package com.delivery.app.utils;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.google.maps.PlacesApi;

import java.io.IOException;

@AllArgsConstructor
@Service
public class GoogleMapsService {

    private final GeoApiContext geoApiContext;


    public PlacesSearchResult[] searchNearby(double lat, double lng, int radius, String keyword) throws Exception {
        LatLng location = new LatLng(lat, lng);
        PlacesSearchResponse response = PlacesApi.nearbySearchQuery(geoApiContext, location)
                .radius(radius) // radio en metros
                .keyword(keyword)
                .await();

        return response.results;
    }

    public DistanceMatrix calculateDistance(String[] origins, String[] destinations) throws Exception {

        return DistanceMatrixApi.newRequest(geoApiContext)
                .origins(origins)
                .destinations(destinations)
                .await();
    }

    public GeocodingResult[] reverseGeocode(double latitude, double longitude) throws ApiException, InterruptedException, IOException {
        return GeocodingApi.reverseGeocode(geoApiContext, new com.google.maps.model.LatLng(latitude, longitude))
                .resultType(AddressType.ROUTE, AddressType.STREET_ADDRESS)
                .await();
    }

}
