package com.smartinventory.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.smartinventory.api.dto.ProductRequest;
import com.smartinventory.api.exception.ProductNotFoundException;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductServiceTest {

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService();
    }

    @Test
    void shouldCreateAndRetrieveProduct() {
        ProductRequest request = new ProductRequest("Keyboard", "Mechanical", new BigDecimal("45.50"), 10);

        Long productId = productService.createProduct(request).getId();

        assertEquals("Keyboard", productService.getProductById(productId).getName());
        assertEquals(1, productService.getAllProducts().size());
    }

    @Test
    void shouldThrowWhenProductDoesNotExist() {
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(999L));
    }
}
