package com.delivery.app.pos.order.repositories;

import com.delivery.app.pos.order.models.PosOrderLineProductAttributeValueRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PosOrderLineProductAttributeValueRelRepository extends JpaRepository<PosOrderLineProductAttributeValueRel, Integer> {

}
