package com.example.order;

import com.example.order.dto.ProductStockRequest;
import com.example.order.dto.ProductStockResponse;
import com.example.order.service.StockResponseManager;
import com.example.order.syncservice.OrderResponse;
import com.example.order.syncservice.OrderService;
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
    private final OrderService orderService;

    public OrderController(
            OrderKafkaProducer producer,
            StockResponseManager responseManager,
            OrderService orderService
    ) {
        this.producer = producer;
        this.responseManager = responseManager;
        this.orderService = orderService;
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

    @PostMapping("/status")
    public ResponseEntity<OrderResponse> checkOrderStatus(
            @RequestBody ProductStockRequest request) {

        orderService.createPendingOrder(request);

        producer.sendStockCheck(request);

        OrderResponse response =
                new OrderResponse(
                        request.orderId(),
                        "PENDING"
                );

        return ResponseEntity
                .accepted()
                .body(response);
    }


    @PostMapping("/final-status")
    public ResponseEntity<OrderResponse> finalOrderStatus(
            @RequestBody ProductStockRequest request) {

        OrderResponse response =
                new OrderResponse(
                        request.orderId(),
                        String.valueOf(orderService.getStatus(request.orderId()))
                );

        return ResponseEntity
                .accepted()
                .body(response);
    }
}
