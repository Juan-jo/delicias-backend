package com.delivery.app.delicias.deliveryzone.repository;

import com.delivery.app.delicias.deliveryzone.model.DeliveryZone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryZoneRepository extends JpaRepository<DeliveryZone, Integer> {

    @Query("""
            SELECT      r
                FROM    DeliveryZone r
                WHERE   (:name IS NULL OR UPPER(r.name) LIKE %:name%)
            """)
    Page<DeliveryZone> findByFilter(
            String name,
            Pageable pageable
    );

    @Query(value = """
            SELECT      *
                FROM    mobile_delivery_zone
                WHERE   ST_Contains(area, ST_SetSRID(ST_Point(:lng, :lat), 4326))
                ORDER BY ST_Distance(ST_Centroid(area), ST_SetSRID(ST_Point(:lng, :lat), 4326)) ASC
                LIMIT   1
        """, nativeQuery = true)
    List<DeliveryZone> findZonesContainingPoint(@Param("lat") double lat, @Param("lng") double lng);

}
