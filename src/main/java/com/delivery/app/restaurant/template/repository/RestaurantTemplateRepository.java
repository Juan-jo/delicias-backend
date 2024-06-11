package com.delivery.app.restaurant.template.repository;

import com.delivery.app.restaurant.template.model.RestaurantTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
