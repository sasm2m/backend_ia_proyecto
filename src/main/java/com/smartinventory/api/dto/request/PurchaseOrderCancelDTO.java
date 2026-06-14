package com.smartinventory.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PurchaseOrderCancelDTO {

    @NotBlank
    @Size(max = 500)
    private String reason;
}
