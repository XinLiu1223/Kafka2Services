package com.example.order;

import com.example.order.dto.ProductStockRequest;
import com.example.order.dto.ProductStockResponse;
import com.example.order.service.StockResponseManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderKafkaProducer producer;
    private final StockResponseManager responseManager;

    public OrderController(
            OrderKafkaProducer producer,
            StockResponseManager responseManager
    ) {
        this.producer = producer;
        this.responseManager = responseManager;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<ProductStockResponse>> createOrder(
            @RequestBody ProductStockRequest request) {

        producer.sendStockCheck(request);
        System.out.println("Order request sent - orderId: " + request.orderId());

        CompletableFuture<ProductStockResponse> future =
                responseManager.register(request.orderId());

        return future
                .orTimeout(5, TimeUnit.SECONDS)
                .thenApply(ResponseEntity::ok
                )
                .exceptionally(ex ->
                        ResponseEntity
                                .status(HttpStatus.GATEWAY_TIMEOUT)
                                .build()
                );
    }
}
