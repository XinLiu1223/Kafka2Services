package com.example.order.syncservice;

import com.example.order.dto.ProductStockRequest;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {

    private final Map<String, OrderStatus> orders =
            new ConcurrentHashMap<>();

    public void createPendingOrder(
            ProductStockRequest request) {

        orders.put(
                request.orderId(),
                OrderStatus.PENDING
        );
    }

    public void markConfirmed(String orderId) {

        orders.put(
                orderId,
                OrderStatus.CONFIRMED
        );

        System.out.println(
                "Order " + orderId + " CONFIRMED"
        );
    }

    public void markOutOfStock(String orderId) {

        orders.put(
                orderId,
                OrderStatus.OUT_OF_STOCK
        );

        System.out.println(
                "Order " + orderId + " OUT_OF_STOCK"
        );
    }

    public OrderStatus getStatus(String orderId) {

        return orders.get(orderId);
    }
}
