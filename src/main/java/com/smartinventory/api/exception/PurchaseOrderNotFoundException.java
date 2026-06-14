package com.smartinventory.api.exception;

public class PurchaseOrderNotFoundException extends RuntimeException {

    public PurchaseOrderNotFoundException(Long id) {
        super("Purchase order with id " + id + " was not found");
    }
}
