package com.smartinventory.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PurchaseOrderRequestDTO {

    @NotNull
    private Long productId;

    @NotNull
    private Long supplierId;

    @NotNull
    @Min(1)
    private Integer quantity;

    private String notes;
}
