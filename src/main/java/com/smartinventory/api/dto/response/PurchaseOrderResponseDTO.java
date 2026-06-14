package com.smartinventory.api.dto.response;

import com.smartinventory.api.enums.OrderStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PurchaseOrderResponseDTO {

    private final Long id;
    private final Long productId;
    private final String productName;
    private final Long supplierId;
    private final String supplierName;
    private final Integer quantity;
    private final OrderStatus status;
    private final boolean automatic;
    private final Long stockAlertId;
    private final LocalDateTime createdAt;
    private final LocalDateTime sentAt;
    private final LocalDateTime confirmedAt;
    private final LocalDate estimatedDeliveryDate;
    private final LocalDateTime receivedAt;
    private final LocalDateTime cancelledAt;
    private final String cancelReason;
    private final String notes;
}
