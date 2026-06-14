package com.smartinventory.api.dto.response;

import com.smartinventory.api.enums.OrderStatus;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PendingOrderReportDTO {

    private final Long orderId;
    private final String productName;
    private final String supplierName;
    private final Integer quantityOrdered;
    private final OrderStatus status;
    private final long daysPending;
    private final LocalDate estimatedDeliveryDate;
}
