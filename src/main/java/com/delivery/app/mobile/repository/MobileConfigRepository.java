package com.delivery.app.mobile.repository;

import com.delivery.app.mobile.model.MobileConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobileConfigRepository extends JpaRepository<MobileConfig, Integer> {


}
