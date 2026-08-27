package com.example.order;

import com.example.order.dto.ProductStockResponse;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductStockResponseConsumer {

    @KafkaListener(
            topics = "product-stock-response",
            groupId = "order-management-group"
    )
    public void receiveStockResponse(ProductStockResponse response) {
        System.out.println("Received stock response: " + response);

        if (response.available()) {
            System.out.println("Order " + response.orderId() + " can proceed.");
        } else {
            System.out.println("Order " + response.orderId() + " cannot proceed.");
        }
    }
}
