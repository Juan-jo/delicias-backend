package com.delivery.app.product.category.repository;

import com.delivery.app.product.category.model.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {

    @Query("""
            SELECT      c
                FROM    ProductCategory c
                WHERE   ( :name IS NULL OR UPPER(c.name) LIKE %:name% )
            """)
    Page<ProductCategory> findByFilter(
            String name,
            Pageable pageable
    );
}
