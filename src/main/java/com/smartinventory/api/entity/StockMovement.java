package com.smartinventory.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "stock_movements")
public class StockMovement extends AuditableEntity {

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long purchaseOrderId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", insertable = false, updatable = false)
    private PurchaseOrder purchaseOrder;
}
