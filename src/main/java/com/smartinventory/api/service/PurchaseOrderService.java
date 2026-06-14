package com.smartinventory.api.service;

import com.smartinventory.api.dto.request.PurchaseOrderCancelDTO;
import com.smartinventory.api.dto.request.PurchaseOrderConfirmDTO;
import com.smartinventory.api.dto.request.PurchaseOrderRequestDTO;
import com.smartinventory.api.dto.response.PendingOrderReportDTO;
import com.smartinventory.api.dto.response.PurchaseOrderResponseDTO;
import com.smartinventory.api.dto.response.SupplierDeliveryAvgDTO;
import com.smartinventory.api.entity.Product;
import com.smartinventory.api.entity.ProductSupplier;
import com.smartinventory.api.entity.PurchaseOrder;
import com.smartinventory.api.entity.StockAlert;
import com.smartinventory.api.entity.StockMovement;
import com.smartinventory.api.enums.OrderStatus;
import com.smartinventory.api.exception.BusinessException;
import com.smartinventory.api.exception.PurchaseOrderNotFoundException;
import com.smartinventory.api.repository.ProductRepository;
import com.smartinventory.api.repository.ProductSupplierRepository;
import com.smartinventory.api.repository.PurchaseOrderRepository;
import com.smartinventory.api.repository.StockAlertRepository;
import com.smartinventory.api.repository.StockMovementRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "smartinventory.features.purchase-orders.enabled", havingValue = "true", matchIfMissing = false)
public class PurchaseOrderService {

    private static final List<OrderStatus> ACTIVE_STATUSES = List.of(
            OrderStatus.DRAFT,
            OrderStatus.SENT,
            OrderStatus.CONFIRMED);

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductRepository productRepository;
    private final ProductSupplierRepository productSupplierRepository;
    private final StockAlertRepository stockAlertRepository;
    private final StockMovementRepository stockMovementRepository;

    public PurchaseOrderResponseDTO create(PurchaseOrderRequestDTO request) {
        Product product = getProduct(request.getProductId());
        validateSupplierLink(request.getProductId(), request.getSupplierId());

        ProductSupplier productSupplier = productSupplierRepository
                .findFirstByProductIdAndActiveTrueOrderByDeliveryDaysAsc(request.getProductId())
                .filter(link -> link.getSupplierId().equals(request.getSupplierId()))
                .orElseThrow(() -> new BusinessException("Supplier is not linked to the product"));

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setProductId(product.getId());
        purchaseOrder.setProductName(product.getName());
        purchaseOrder.setSupplierId(productSupplier.getSupplierId());
        purchaseOrder.setSupplierName(productSupplier.getSupplier().getName());
        purchaseOrder.setQuantity(request.getQuantity());
        purchaseOrder.setStatus(OrderStatus.DRAFT);
        purchaseOrder.setAutomatic(false);
        purchaseOrder.setNotes(request.getNotes());

        PurchaseOrder savedOrder = purchaseOrderRepository.save(purchaseOrder);
        log.info("Manual purchase order {} created for product {}", savedOrder.getId(), savedOrder.getProductId());
        return toResponse(savedOrder);
    }

    public Optional<PurchaseOrderResponseDTO> createAutomaticIfNeeded(Product product, StockAlert alert) {
        boolean alreadyExists = purchaseOrderRepository.existsByProductIdAndStatusIn(
                product.getId(),
                ACTIVE_STATUSES);
        if (alreadyExists) {
            log.info("Automatic order skipped for product {} because an active order already exists", product.getId());
            return Optional.empty();
        }

        Optional<ProductSupplier> selectedSupplier = productSupplierRepository
                .findFirstByProductIdAndActiveTrueOrderByDeliveryDaysAsc(product.getId());
        if (selectedSupplier.isEmpty()) {
            log.warn("Automatic order skipped for product {} because no active supplier was found", product.getId());
            return Optional.empty();
        }

        ProductSupplier productSupplier = selectedSupplier.get();
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setProductId(product.getId());
        purchaseOrder.setProductName(product.getName());
        purchaseOrder.setSupplierId(productSupplier.getSupplierId());
        purchaseOrder.setSupplierName(productSupplier.getSupplier().getName());
        purchaseOrder.setQuantity(alert.getMinimumStock() * 2);
        purchaseOrder.setStatus(OrderStatus.DRAFT);
        purchaseOrder.setAutomatic(true);
        purchaseOrder.setStockAlertId(alert.getId());
        purchaseOrder.setStockAlert(alert);

        PurchaseOrder savedOrder = purchaseOrderRepository.save(purchaseOrder);
        log.info("Automatic purchase order {} created for product {}", savedOrder.getId(), savedOrder.getProductId());
        return Optional.of(toResponse(savedOrder));
    }

    public PurchaseOrderResponseDTO send(Long id) {
        PurchaseOrder purchaseOrder = getPurchaseOrder(id);
        validateTransition(purchaseOrder.getStatus(), OrderStatus.SENT);
        purchaseOrder.setStatus(OrderStatus.SENT);
        purchaseOrder.setSentAt(LocalDateTime.now());
        PurchaseOrder savedOrder = purchaseOrderRepository.save(purchaseOrder);
        log.info("Purchase order {} sent successfully", savedOrder.getId());
        return toResponse(savedOrder);
    }

    public PurchaseOrderResponseDTO confirm(Long id, PurchaseOrderConfirmDTO request) {
        PurchaseOrder purchaseOrder = getPurchaseOrder(id);
        validateTransition(purchaseOrder.getStatus(), OrderStatus.CONFIRMED);
        purchaseOrder.setStatus(OrderStatus.CONFIRMED);
        purchaseOrder.setConfirmedAt(LocalDateTime.now());
        purchaseOrder.setEstimatedDeliveryDate(request.getEstimatedDeliveryDate());
        PurchaseOrder savedOrder = purchaseOrderRepository.save(purchaseOrder);
        log.info("Purchase order {} confirmed successfully", savedOrder.getId());
        return toResponse(savedOrder);
    }

    @Transactional
    public PurchaseOrderResponseDTO receive(Long id) {
        PurchaseOrder purchaseOrder = getPurchaseOrder(id);
        validateTransition(purchaseOrder.getStatus(), OrderStatus.RECEIVED);
        purchaseOrder.setStatus(OrderStatus.RECEIVED);
        purchaseOrder.setReceivedAt(LocalDateTime.now());

        Product product = getProduct(purchaseOrder.getProductId());
        product.setStock(product.getStock() + purchaseOrder.getQuantity());
        productRepository.save(product);

        StockMovement stockMovement = new StockMovement();
        stockMovement.setProductId(product.getId());
        stockMovement.setPurchaseOrderId(purchaseOrder.getId());
        stockMovement.setQuantity(purchaseOrder.getQuantity());
        stockMovement.setType("ENTRY");
        stockMovement.setReason("PURCHASE");
        stockMovementRepository.save(stockMovement);

        if (purchaseOrder.getStockAlertId() != null) {
            stockAlertRepository.findById(purchaseOrder.getStockAlertId())
                    .filter(StockAlert::isActive)
                    .ifPresent(alert -> {
                        alert.setActive(false);
                        alert.setResolvedAt(LocalDateTime.now());
                        alert.setResolutionOrderId(purchaseOrder.getId());
                        stockAlertRepository.save(alert);
                    });
        }

        PurchaseOrder savedOrder = purchaseOrderRepository.save(purchaseOrder);
        log.info("Purchase order {} received successfully", savedOrder.getId());
        return toResponse(savedOrder);
    }

    public PurchaseOrderResponseDTO cancel(Long id, PurchaseOrderCancelDTO request) {
        PurchaseOrder purchaseOrder = getPurchaseOrder(id);
        validateTransition(purchaseOrder.getStatus(), OrderStatus.CANCELLED);
        purchaseOrder.setStatus(OrderStatus.CANCELLED);
        purchaseOrder.setCancelledAt(LocalDateTime.now());
        purchaseOrder.setCancelReason(request.getReason());
        PurchaseOrder savedOrder = purchaseOrderRepository.save(purchaseOrder);
        log.info("Purchase order {} cancelled successfully", savedOrder.getId());
        return toResponse(savedOrder);
    }

    public void validateTransition(OrderStatus currentStatus, OrderStatus targetStatus) {
        boolean valid = switch (currentStatus) {
            case DRAFT -> targetStatus == OrderStatus.SENT || targetStatus == OrderStatus.CANCELLED;
            case SENT -> targetStatus == OrderStatus.CONFIRMED || targetStatus == OrderStatus.CANCELLED;
            case CONFIRMED -> targetStatus == OrderStatus.RECEIVED || targetStatus == OrderStatus.CANCELLED;
            case RECEIVED, CANCELLED -> false;
        };

        if (!valid) {
            throw new BusinessException(
                    "Invalid purchase order transition from " + currentStatus + " to " + targetStatus);
        }
    }

    public Page<PurchaseOrderResponseDTO> list(Pageable pageable) {
        return purchaseOrderRepository.findAll(pageable).map(this::toResponse);
    }

    public PurchaseOrderResponseDTO getById(Long id) {
        return toResponse(getPurchaseOrder(id));
    }

    public List<PendingOrderReportDTO> getPendingOrdersReport() {
        return purchaseOrderRepository.findPendingOrders()
                .stream()
                .map(order -> new PendingOrderReportDTO(
                        order.getId(),
                        order.getProductName(),
                        order.getSupplierName(),
                        order.getQuantity(),
                        order.getStatus(),
                        ChronoUnit.DAYS.between(order.getCreatedAt().toLocalDate(), LocalDateTime.now().toLocalDate()),
                        order.getEstimatedDeliveryDate()))
                .toList();
    }

    public List<SupplierDeliveryAvgDTO> getSupplierDeliveryAvg() {
        return purchaseOrderRepository.findAvgDeliveryTimeBySupplier();
    }

    private void validateSupplierLink(Long productId, Long supplierId) {
        boolean linked = productSupplierRepository.existsByProductIdAndSupplierIdAndActiveTrue(productId, supplierId);
        if (!linked) {
            throw new BusinessException("Supplier is not linked to the product");
        }
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("Product with id " + productId + " was not found"));
    }

    private PurchaseOrder getPurchaseOrder(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new PurchaseOrderNotFoundException(id));
    }

    private PurchaseOrderResponseDTO toResponse(PurchaseOrder purchaseOrder) {
        return PurchaseOrderResponseDTO.builder()
                .id(purchaseOrder.getId())
                .productId(purchaseOrder.getProductId())
                .productName(purchaseOrder.getProductName())
                .supplierId(purchaseOrder.getSupplierId())
                .supplierName(purchaseOrder.getSupplierName())
                .quantity(purchaseOrder.getQuantity())
                .status(purchaseOrder.getStatus())
                .automatic(purchaseOrder.isAutomatic())
                .stockAlertId(purchaseOrder.getStockAlertId())
                .createdAt(purchaseOrder.getCreatedAt())
                .sentAt(purchaseOrder.getSentAt())
                .confirmedAt(purchaseOrder.getConfirmedAt())
                .estimatedDeliveryDate(purchaseOrder.getEstimatedDeliveryDate())
                .receivedAt(purchaseOrder.getReceivedAt())
                .cancelledAt(purchaseOrder.getCancelledAt())
                .cancelReason(purchaseOrder.getCancelReason())
                .notes(purchaseOrder.getNotes())
                .build();
    }
}
