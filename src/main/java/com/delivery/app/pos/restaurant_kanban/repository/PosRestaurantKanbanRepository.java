package com.delivery.app.pos.restaurant_kanban.repository;

import com.delivery.app.pos.restaurant_kanban.model.PosRestaurantKanban;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PosRestaurantKanbanRepository extends JpaRepository<PosRestaurantKanban, Integer> {

    List<PosRestaurantKanban> findByRestaurantTmplId(Integer restaurantId);


    @Query(value = """
            select 'deliverer' as "typePoint", cast(d.deliverer_id as text) as "name", ST_AsText(d.last_position) as "position"  from deliverers d
            union
            select 'restaurant' as "typePoint", rt."name" as "name", ST_AsText(rt."position") as "position" from restaurant_template rt
            union
            select 'home' as "typePoint", cast(ua.keycloak_user_id as text) as "name", ST_AsText(ua."position") as "position" from user_addresses ua;
            """, nativeQuery = true)
    List<Tuple> deliveryTracking();
}
