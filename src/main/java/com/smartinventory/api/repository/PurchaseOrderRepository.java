package com.smartinventory.api.repository;

import com.smartinventory.api.dto.response.SupplierDeliveryAvgDTO;
import com.smartinventory.api.entity.PurchaseOrder;
import com.smartinventory.api.enums.OrderStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    boolean existsByProductIdAndStatusIn(Long productId, List<OrderStatus> statuses);

    Page<PurchaseOrder> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);

    @Query("""
            select po
            from PurchaseOrder po
            where po.status in (com.smartinventory.api.enums.OrderStatus.SENT,
                                com.smartinventory.api.enums.OrderStatus.CONFIRMED)
            order by po.createdAt desc
            """)
    List<PurchaseOrder> findPendingOrders();

    @Query("""
            select new com.smartinventory.api.dto.response.SupplierDeliveryAvgDTO(
                po.supplierId,
                po.supplierName,
                avg(timestampdiff(day, po.sentAt, po.receivedAt)),
                count(po.id)
            )
            from PurchaseOrder po
            where po.status = com.smartinventory.api.enums.OrderStatus.RECEIVED
              and po.sentAt is not null
              and po.receivedAt is not null
            group by po.supplierId, po.supplierName
            order by po.supplierName asc
            """)
    List<SupplierDeliveryAvgDTO> findAvgDeliveryTimeBySupplier();
}
