package com.example.order.dto;

public record ProductStockResponse(
        String orderId,
        String productId,
        int requestedQuantity,
        int availableQuantity,
        boolean available
) {
}
