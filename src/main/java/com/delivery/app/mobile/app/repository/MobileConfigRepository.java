package com.delivery.app.mobile.app.repository;

import com.delivery.app.mobile.app.model.MobileConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobileConfigRepository extends JpaRepository<MobileConfig, Integer> {


}
