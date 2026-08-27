package com.example.order.syncservice;

public record OrderResponse(
        String orderId,
        String status
) {
}
