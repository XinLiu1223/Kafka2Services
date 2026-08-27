package com.example.order.dto;

public record ProductStockRequest(
        String orderId,
        String productId,
        int quantity
) {
}
