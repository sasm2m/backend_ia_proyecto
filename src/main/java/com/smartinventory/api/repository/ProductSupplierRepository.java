package com.smartinventory.api.repository;

import com.smartinventory.api.entity.ProductSupplier;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSupplierRepository extends JpaRepository<ProductSupplier, Long> {

    boolean existsByProductIdAndSupplierIdAndActiveTrue(Long productId, Long supplierId);

    Optional<ProductSupplier> findFirstByProductIdAndActiveTrueOrderByDeliveryDaysAsc(Long productId);
}
