package com.smartinventory.api.service;

import com.smartinventory.api.dto.ProductRequest;
import com.smartinventory.api.dto.ProductResponse;
import com.smartinventory.api.exception.ProductNotFoundException;
import com.smartinventory.api.model.Product;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final Map<Long, Product> products = new LinkedHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    public ProductResponse createProduct(ProductRequest request) {
        Long id = idSequence.getAndIncrement();
        Product product = new Product(
                id,
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStock());
        products.put(id, product);
        return toResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        List<ProductResponse> responses = new ArrayList<>();
        for (Product product : products.values()) {
            responses.add(toResponse(product));
        }
        return responses;
    }

    public ProductResponse getProductById(Long id) {
        return toResponse(getExistingProduct(id));
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = getExistingProduct(id);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        return toResponse(product);
    }

    public void deleteProduct(Long id) {
        Product product = getExistingProduct(id);
        products.remove(product.getId());
    }

    private Product getExistingProduct(Long id) {
        Product product = products.get(id);
        if (product == null) {
            throw new ProductNotFoundException(id);
        }
        return product;
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock());
    }
}
