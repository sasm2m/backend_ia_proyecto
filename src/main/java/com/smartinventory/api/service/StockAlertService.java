package com.smartinventory.api.service;

import com.smartinventory.api.entity.Product;
import com.smartinventory.api.entity.StockAlert;
import com.smartinventory.api.repository.StockAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "smartinventory.features.purchase-orders.enabled", havingValue = "true", matchIfMissing = false)
public class StockAlertService {

    private final StockAlertRepository stockAlertRepository;
    private final PurchaseOrderService purchaseOrderService;

    public StockAlert checkAndCreateAlert(Product product, StockAlert stockAlert) {
        StockAlert savedAlert = stockAlertRepository.save(stockAlert);
        purchaseOrderService.createAutomaticIfNeeded(product, savedAlert);
        return savedAlert;
    }
}
