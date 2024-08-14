package com.delivery.app.restaurant.schedule.repository;

import com.delivery.app.restaurant.schedule.model.RestaurantTmplSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantTmplScheduleRepository extends JpaRepository<RestaurantTmplSchedule, Integer> {

}
