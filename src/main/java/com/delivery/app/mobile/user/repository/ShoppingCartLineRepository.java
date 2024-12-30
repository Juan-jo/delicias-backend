package com.delivery.app.mobile.user.repository;

import com.delivery.app.mobile.user.models.ShoppingCartLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShoppingCartLineRepository extends JpaRepository<ShoppingCartLine, UUID> {
}
