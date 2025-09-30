package com.delivery.app.mobile.shopping.repository;

import com.delivery.app.mobile.shopping.model.ShoppingCartLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShoppingCartLineRepository extends JpaRepository<ShoppingCartLine, UUID> {
}
