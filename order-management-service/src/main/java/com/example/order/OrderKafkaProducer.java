package com.example.order;

import com.example.order.dto.ProductStockRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderKafkaProducer {

    private final KafkaTemplate<String, ProductStockRequest> kafkaTemplate;

    public OrderKafkaProducer(KafkaTemplate<String, ProductStockRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendStockCheck(
            ProductStockRequest request
    ) {
        System.out.println("Sending stock check request: " + request);

        kafkaTemplate.send(
                "product-stock-check",
                request.orderId(),
                request
        );
    }
}
