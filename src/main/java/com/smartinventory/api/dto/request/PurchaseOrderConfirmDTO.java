package com.smartinventory.api.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PurchaseOrderConfirmDTO {

    @NotNull
    private LocalDate estimatedDeliveryDate;
}
