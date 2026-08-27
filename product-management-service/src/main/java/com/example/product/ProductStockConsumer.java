package com.example.product;

import com.example.product.dto.CheckProductStockResponse;
import com.example.product.dto.ProductStockCheckRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductStockConsumer {

    private final KafkaTemplate<String, CheckProductStockResponse> kafkaTemplate;

    public ProductStockConsumer(
            KafkaTemplate<String, CheckProductStockResponse> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            topics = "product-stock-check",
            groupId = "product-management-group"
    )
    public void checkStock(ProductStockCheckRequest request) {
        int availableStock = 10;

        boolean available =
                availableStock >= request.quantity();

        CheckProductStockResponse response = new CheckProductStockResponse(
                request.orderId(),
                request.productId(),
                request.quantity(),
                availableStock,
                available
        );
        System.out.println("Received stock response: " + response);

        kafkaTemplate.send(
                "product-stock-response",
                request.orderId(),
                response
        );
    }
}
