package com.delivery.app.security.repository;

import com.delivery.app.security.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Integer> {

    List<UserAddress> findByKeycloakUserId(UUID keycloakUserId);
}
