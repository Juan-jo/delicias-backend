package com.delivery.app.product.attribute.repositories;

import com.delivery.app.product.attribute.models.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Integer> {

}
