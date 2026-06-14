package com.smartinventory.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SupplierDeliveryAvgDTO {

    private final Long supplierId;
    private final String supplierName;
    private final Double avgDeliveryDays;
    private final Long totalOrdersReceived;
}
