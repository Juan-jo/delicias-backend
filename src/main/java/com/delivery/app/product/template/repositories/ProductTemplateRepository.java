package com.delivery.app.product.template.repositories;

import com.delivery.app.product.template.models.ProductTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductTemplateRepository extends JpaRepository<ProductTemplate, Integer> {

    List<ProductTemplate> findByIdIn(Collection<Integer> tmplIds);

}
