package com.delivery.app.pos.order.repositories;


import com.delivery.app.pos.order.models.PosOrder;
import com.delivery.app.pos.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PosOrderRepository extends JpaRepository<PosOrder, Integer> {

    String querySearchFilter = """
                SELECT      o
                    FROM    PosOrder o
                    WHERE   :status IS NULL OR o.status = :status
            """;

    @Query(querySearchFilter)
    Page<PosOrder> filterRestaurant(
            OrderStatus status,
            Pageable pageable
    );

}
