package com.delivery.app.pos.restaurant_kanban.repository;

import com.delivery.app.pos.restaurant_kanban.model.PosRestaurantKanban;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PosRestaurantKanbanRepository extends JpaRepository<PosRestaurantKanban, Integer> {

    List<PosRestaurantKanban> findByRestaurantTmplId(Integer restaurantId);
}
