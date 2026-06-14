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
@Table(name = "product_suppliers")
public class ProductSupplier extends AuditableEntity {

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long supplierId;

    @Column(nullable = false)
    private Integer deliveryDays;

    @Column(nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", insertable = false, updatable = false)
    private Supplier supplier;
}
