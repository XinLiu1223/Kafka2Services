package com.example.product.dto;

public record CheckProductStockResponse(
        String orderId,
        String productId,
        int requestedQuantity,
        int availableQuantity,
        boolean available
) {
}
