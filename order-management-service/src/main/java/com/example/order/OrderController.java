package com.example.order;

import com.example.order.dto.ProductStockRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderKafkaProducer producer;

    public OrderController(OrderKafkaProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(
            @RequestBody ProductStockRequest request) {

        producer.sendStockCheck(request);

        return ResponseEntity.accepted()
                .body("Order request accepted: " + request.orderId());
    }
}
