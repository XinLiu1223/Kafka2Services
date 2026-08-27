package com.example.product.dto;

public record ProductStockCheckRequest(
        String orderId,
        String productId,
        int quantity
) {
}
