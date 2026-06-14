package com.smartinventory.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "suppliers")
public class Supplier extends AuditableEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY)
    private List<ProductSupplier> productSuppliers = new ArrayList<>();
}
