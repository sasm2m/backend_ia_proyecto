package com.smartinventory.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "stock_alerts")
public class StockAlert extends AuditableEntity {

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer currentStock;

    @Column(nullable = false)
    private Integer minimumStock;

    @Column(nullable = false)
    private boolean active;

    private LocalDateTime resolvedAt;

    private Long resolutionOrderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
}
