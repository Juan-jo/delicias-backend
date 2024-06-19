package com.delivery.app.product.template.repositories;

import com.delivery.app.product.template.models.ViewProductTmplAttrValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViewProductTmplAttrValueRepository extends JpaRepository<ViewProductTmplAttrValue, Integer> {

    List<ViewProductTmplAttrValue> findByProductTmplId(Integer tmplId);
}
