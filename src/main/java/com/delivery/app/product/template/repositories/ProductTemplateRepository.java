package com.delivery.app.product.template.repositories;

import com.delivery.app.product.template.models.ProductTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductTemplateRepository extends JpaRepository<ProductTemplate, Integer> {

    List<ProductTemplate> findByIdIn(Collection<Integer> tmplIds);

    @Query("""
            SELECT      p
                FROM    ProductTemplate p
                WHERE   ( :name IS NULL OR UPPER(p.name) LIKE %:name% )
                        AND
                        ( :restaurantId IS NULL OR p.restaurantTmpl.id = :restaurantId )
            """)
    Page<ProductTemplate> searchFilter(
            String name,
            Integer restaurantId,
            Pageable pageable
    );
}
