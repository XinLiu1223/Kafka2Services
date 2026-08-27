package com.example.order.service;

import com.example.order.dto.ProductStockResponse;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StockResponseManager {

    private final Map<
            String,
            CompletableFuture<ProductStockResponse>
            > pendingRequests = new ConcurrentHashMap<>();

    public CompletableFuture<ProductStockResponse> register(
            String orderId) {

        CompletableFuture<ProductStockResponse> future =
                new CompletableFuture<>();

        pendingRequests.put(orderId, future);

        return future;
    }

    public void complete(ProductStockResponse response) {

        CompletableFuture<ProductStockResponse> future =
                pendingRequests.remove(response.orderId());

        if (future != null) {
            future.complete(response);
        }
    }
}
