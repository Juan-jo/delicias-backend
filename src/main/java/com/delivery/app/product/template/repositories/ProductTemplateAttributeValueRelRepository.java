package com.delivery.app.product.template.repositories;

import com.delivery.app.product.template.models.ProductTemplateAttributeValueRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductTemplateAttributeValueRelRepository extends JpaRepository<ProductTemplateAttributeValueRel, Integer> {

    @Modifying
    void deleteByAttributeValueId(Integer id);

    Optional<ProductTemplateAttributeValueRel> findByTemplateIdAndAttributeValueId(Integer productTmplId, Integer attrValueId);

}
