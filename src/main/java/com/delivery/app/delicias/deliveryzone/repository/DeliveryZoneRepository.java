package com.delivery.app.delicias.deliveryzone.repository;

import com.delivery.app.delicias.deliveryzone.model.DeliveryZone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryZoneRepository extends JpaRepository<DeliveryZone, Integer> {

    @Query("""
            SELECT      r
                FROM    DeliveryZone r
                WHERE   :name IS NULL OR UPPER(r.name) LIKE %:name%
            """)
    Page<DeliveryZone> findByFilter(
            String name,
            Pageable pageable
    );

}
