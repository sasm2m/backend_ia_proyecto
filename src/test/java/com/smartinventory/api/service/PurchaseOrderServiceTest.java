package com.smartinventory.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartinventory.api.dto.request.PurchaseOrderCancelDTO;
import com.smartinventory.api.dto.response.PurchaseOrderResponseDTO;
import com.smartinventory.api.entity.Product;
import com.smartinventory.api.entity.ProductSupplier;
import com.smartinventory.api.entity.PurchaseOrder;
import com.smartinventory.api.entity.StockAlert;
import com.smartinventory.api.entity.StockMovement;
import com.smartinventory.api.entity.Supplier;
import com.smartinventory.api.enums.OrderStatus;
import com.smartinventory.api.exception.BusinessException;
import com.smartinventory.api.repository.ProductRepository;
import com.smartinventory.api.repository.ProductSupplierRepository;
import com.smartinventory.api.repository.PurchaseOrderRepository;
import com.smartinventory.api.repository.StockAlertRepository;
import com.smartinventory.api.repository.StockMovementRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductSupplierRepository productSupplierRepository;

    @Mock
    private StockAlertRepository stockAlertRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;

    @Captor
    private ArgumentCaptor<PurchaseOrder> purchaseOrderCaptor;

    @Captor
    private ArgumentCaptor<StockMovement> stockMovementCaptor;

    @Captor
    private ArgumentCaptor<StockAlert> stockAlertCaptor;

    private Product product;
    private StockAlert alert;
    private ProductSupplier productSupplier;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Keyboard");
        product.setPrice(new BigDecimal("10.00"));
        product.setStock(2);
        product.setMinimumStock(5);

        alert = new StockAlert();
        alert.setId(11L);
        alert.setProductId(1L);
        alert.setMinimumStock(5);
        alert.setCurrentStock(2);
        alert.setActive(true);

        Supplier supplier = new Supplier();
        supplier.setId(7L);
        supplier.setName("Fast Supplier");
        supplier.setActive(true);

        productSupplier = new ProductSupplier();
        productSupplier.setProductId(1L);
        productSupplier.setSupplierId(7L);
        productSupplier.setDeliveryDays(3);
        productSupplier.setActive(true);
        productSupplier.setSupplier(supplier);
    }

    @Test
    void createAutomaticIfNeeded_withNoActiveOrder_shouldCreateDraftOrder() {
        when(purchaseOrderRepository.existsByProductIdAndStatusIn(any(), any())).thenReturn(false);
        when(productSupplierRepository.findFirstByProductIdAndActiveTrueOrderByDeliveryDaysAsc(1L))
                .thenReturn(Optional.of(productSupplier));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(invocation -> {
            PurchaseOrder purchaseOrder = invocation.getArgument(0);
            purchaseOrder.setId(99L);
            purchaseOrder.setCreatedAt(LocalDateTime.now());
            return purchaseOrder;
        });

        Optional<PurchaseOrderResponseDTO> result = purchaseOrderService.createAutomaticIfNeeded(product, alert);

        verify(purchaseOrderRepository).save(purchaseOrderCaptor.capture());
        PurchaseOrder savedOrder = purchaseOrderCaptor.getValue();
        assertTrue(result.isPresent());
        assertTrue(savedOrder.isAutomatic());
        assertEquals(OrderStatus.DRAFT, savedOrder.getStatus());
        assertEquals(10, savedOrder.getQuantity());
    }

    @Test
    void createAutomaticIfNeeded_withExistingActiveOrder_shouldNotCreate() {
        when(purchaseOrderRepository.existsByProductIdAndStatusIn(any(), any())).thenReturn(true);

        Optional<PurchaseOrderResponseDTO> result = purchaseOrderService.createAutomaticIfNeeded(product, alert);

        assertFalse(result.isPresent());
        verify(purchaseOrderRepository, never()).save(any(PurchaseOrder.class));
    }

    @Test
    void createAutomaticIfNeeded_withNoActiveSupplier_shouldNotCreate() {
        when(purchaseOrderRepository.existsByProductIdAndStatusIn(any(), any())).thenReturn(false);
        when(productSupplierRepository.findFirstByProductIdAndActiveTrueOrderByDeliveryDaysAsc(1L))
                .thenReturn(Optional.empty());

        Optional<PurchaseOrderResponseDTO> result = purchaseOrderService.createAutomaticIfNeeded(product, alert);

        assertFalse(result.isPresent());
        verify(purchaseOrderRepository, never()).save(any(PurchaseOrder.class));
    }

    @Test
    void receive_shouldCreateStockMovementAndResolveAlert() {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(21L);
        purchaseOrder.setProductId(1L);
        purchaseOrder.setProductName("Keyboard");
        purchaseOrder.setSupplierId(7L);
        purchaseOrder.setSupplierName("Fast Supplier");
        purchaseOrder.setQuantity(10);
        purchaseOrder.setStatus(OrderStatus.CONFIRMED);
        purchaseOrder.setStockAlertId(11L);
        purchaseOrder.setCreatedAt(LocalDateTime.now().minusDays(1));

        when(purchaseOrderRepository.findById(21L)).thenReturn(Optional.of(purchaseOrder));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(stockAlertRepository.findById(11L)).thenReturn(Optional.of(alert));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PurchaseOrderResponseDTO response = purchaseOrderService.receive(21L);

        verify(stockMovementRepository).save(stockMovementCaptor.capture());
        StockMovement stockMovement = stockMovementCaptor.getValue();
        assertEquals("ENTRY", stockMovement.getType());
        assertEquals("PURCHASE", stockMovement.getReason());
        assertEquals(10, stockMovement.getQuantity());

        verify(stockAlertRepository).save(stockAlertCaptor.capture());
        StockAlert resolvedAlert = stockAlertCaptor.getValue();
        assertFalse(resolvedAlert.isActive());
        assertEquals(21L, resolvedAlert.getResolutionOrderId());
        assertNotNull(resolvedAlert.getResolvedAt());

        assertEquals(OrderStatus.RECEIVED, response.getStatus());
    }

    @Test
    void cancel_onReceivedOrder_shouldThrowBusinessException() {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(30L);
        purchaseOrder.setStatus(OrderStatus.RECEIVED);

        PurchaseOrderCancelDTO cancelDTO = new PurchaseOrderCancelDTO();
        cancelDTO.setReason("Late cancellation");

        when(purchaseOrderRepository.findById(30L)).thenReturn(Optional.of(purchaseOrder));

        assertThrows(BusinessException.class, () -> purchaseOrderService.cancel(30L, cancelDTO));
        verify(purchaseOrderRepository, never()).save(any(PurchaseOrder.class));
    }

    private void assertTrue(boolean value) {
        org.junit.jupiter.api.Assertions.assertTrue(value);
    }
}
