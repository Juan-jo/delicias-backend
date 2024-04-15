package com.delivery.app.pos.order.repositories;

import com.delivery.app.pos.order.models.PosOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PosOrderLineRepository extends JpaRepository<PosOrderLine, Integer> {

}
