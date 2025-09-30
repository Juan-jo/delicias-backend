package com.delivery.app.pos.order.repositories;


import com.delivery.app.pos.order.models.PosOrder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class PosOrderRepositoryImpl implements PosOrderRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void refresh(PosOrder order) {
        entityManager.refresh(order);
    }
}
