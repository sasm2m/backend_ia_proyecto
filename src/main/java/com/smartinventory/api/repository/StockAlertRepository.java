package com.smartinventory.api.repository;

import com.smartinventory.api.entity.StockAlert;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockAlertRepository extends JpaRepository<StockAlert, Long> {

    Optional<StockAlert> findByProductIdAndActiveTrue(Long productId);
}
