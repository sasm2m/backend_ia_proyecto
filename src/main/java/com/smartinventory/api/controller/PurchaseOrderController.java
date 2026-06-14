package com.smartinventory.api.controller;

import com.smartinventory.api.dto.ErrorResponse;
import com.smartinventory.api.dto.request.PurchaseOrderCancelDTO;
import com.smartinventory.api.dto.request.PurchaseOrderConfirmDTO;
import com.smartinventory.api.dto.request.PurchaseOrderRequestDTO;
import com.smartinventory.api.dto.response.PurchaseOrderResponseDTO;
import com.smartinventory.api.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/purchase-orders")
@ConditionalOnProperty(name = "smartinventory.features.purchase-orders.enabled", havingValue = "true")
@Tag(name = "Órdenes de Compra")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a manual purchase order")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Purchase order created"),
            @ApiResponse(responseCode = "400", description = "Invalid request or business rule violation",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product or supplier not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public PurchaseOrderResponseDTO create(@Valid @RequestBody PurchaseOrderRequestDTO request) {
        return purchaseOrderService.create(request);
    }

    @GetMapping
    @Operation(summary = "List purchase orders")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Purchase orders returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Page<PurchaseOrderResponseDTO> list(Pageable pageable) {
        return purchaseOrderService.list(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get purchase order by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Purchase order returned successfully"),
            @ApiResponse(responseCode = "404", description = "Purchase order not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public PurchaseOrderResponseDTO getById(@PathVariable Long id) {
        return purchaseOrderService.getById(id);
    }

    @PatchMapping("/{id}/send")
    @Operation(summary = "Send a purchase order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Purchase order sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid transition",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Purchase order not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public PurchaseOrderResponseDTO send(@PathVariable Long id) {
        return purchaseOrderService.send(id);
    }

    @PatchMapping("/{id}/confirm")
    @Operation(summary = "Confirm a purchase order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Purchase order confirmed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or transition",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Purchase order not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public PurchaseOrderResponseDTO confirm(@PathVariable Long id, @Valid @RequestBody PurchaseOrderConfirmDTO request) {
        return purchaseOrderService.confirm(id, request);
    }

    @PatchMapping("/{id}/receive")
    @Operation(summary = "Receive a purchase order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Purchase order received successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid transition",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Purchase order not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public PurchaseOrderResponseDTO receive(@PathVariable Long id) {
        return purchaseOrderService.receive(id);
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a purchase order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Purchase order cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or transition",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Purchase order not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public PurchaseOrderResponseDTO cancel(@PathVariable Long id, @Valid @RequestBody PurchaseOrderCancelDTO request) {
        return purchaseOrderService.cancel(id, request);
    }
}
