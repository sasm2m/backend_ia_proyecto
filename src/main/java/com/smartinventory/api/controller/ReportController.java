package com.smartinventory.api.controller;

import com.smartinventory.api.dto.response.PendingOrderReportDTO;
import com.smartinventory.api.dto.response.SupplierDeliveryAvgDTO;
import com.smartinventory.api.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
@ConditionalOnProperty(name = "smartinventory.features.purchase-orders.enabled", havingValue = "true")
@Tag(name = "Reports")
public class ReportController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping("/pending-orders")
    @Operation(summary = "Get pending purchase orders report")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pending orders report returned successfully")
    })
    public List<PendingOrderReportDTO> getPendingOrders() {
        return purchaseOrderService.getPendingOrdersReport();
    }

    @GetMapping("/supplier-delivery-avg")
    @Operation(summary = "Get supplier average delivery time report")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supplier delivery average report returned successfully")
    })
    public List<SupplierDeliveryAvgDTO> getSupplierDeliveryAvg() {
        return purchaseOrderService.getSupplierDeliveryAvg();
    }
}
