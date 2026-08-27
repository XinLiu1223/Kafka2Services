package com.example.order;

import com.example.order.dto.ProductStockResponse;
import com.example.order.service.StockResponseManager;
import com.example.order.syncservice.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductStockResponseConsumer {
    private final StockResponseManager responseManager;
    private final OrderService orderService;

    public ProductStockResponseConsumer(
            StockResponseManager responseManager,
            OrderService orderService
    ) {

        this.responseManager = responseManager;
        this.orderService = orderService;
    }

    @KafkaListener(
            topics = "product-stock-response",
            groupId = "order-management-group"
    )
    public void receiveStockResponse(ProductStockResponse response) {
        System.out.println("Received stock response: " + response);

        if (response.available()) {
            System.out.println("Order " + response.orderId() + " can proceed.");

            orderService.markConfirmed(
                    response.orderId()
            );

        } else {
            System.out.println("Order " + response.orderId() + " cannot proceed.");

            orderService.markOutOfStock(
                    response.orderId()
            );
        }

        responseManager.complete(response);
    }
}
