package com.example.order;

import com.example.order.dto.ProductStockResponse;
import com.example.order.service.StockResponseManager;
import com.example.order.syncservice.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ProductStockResponseConsumer {
    private final StockResponseManager responseManager;
    private final OrderService orderService;
    private final RestClient restClient;

    public ProductStockResponseConsumer(
            StockResponseManager responseManager,
            OrderService orderService,
            RestClient restClient
    ) {

        this.responseManager = responseManager;
        this.orderService = orderService;
        this.restClient = restClient;
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


            try {
                String result = restClient
                        .get()
                        .uri(
                                "http://localhost:8080/email?order-id={orderId}",
                                response.orderId()
                        )
                        .retrieve()
                        .body(String.class);

                System.out.println(
                        "Email service response: " + result
                );

            } catch (HttpClientErrorException e) {
                System.err.println(
                        "Email service returned HTTP error: "
                                + e.getStatusCode()
                                + ", body: "
                                + e.getResponseBodyAsString()
                );
            } catch (RestClientException e) {
                System.err.println(
                        "Failed to call Email Service: "
                                + e.getMessage()
                );
            } catch (Exception e) {
                System.err.println(
                        "Unexpected error: "
                                + e.getMessage()
                );
            }

        } else {
            System.out.println("Order " + response.orderId() + " cannot proceed.");

            orderService.markOutOfStock(
                    response.orderId()
            );
        }

        responseManager.complete(response);
    }
}
