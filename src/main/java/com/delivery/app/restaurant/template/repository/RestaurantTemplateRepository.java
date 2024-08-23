package com.delivery.app.restaurant.template.repository;

import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantTemplateRepository extends JpaRepository<RestaurantTemplate, Integer> {

    @Query("""
            SELECT      r
                FROM    RestaurantTemplate r
                WHERE   :name IS NULL OR UPPER(r.name) LIKE %:name%
            """)
    Page<RestaurantTemplate> findByFilter(
            String name,
            Pageable pageable
    );

    @Query(value = """
                SELECT 	CEIL(ST_Distance(r.position ,ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326))) AS distance_meters
                FROM    restaurant_template r
                WHERE   r.id = :restaurantId
 
        """, nativeQuery = true)
    Long distanceMetersTo(@Param("restaurantId") Integer id,
                            @Param("longitude") double lng,
                            @Param("latitude") double lat);

}
