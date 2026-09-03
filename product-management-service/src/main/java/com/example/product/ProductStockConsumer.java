package com.example.product;

import com.example.product.dto.CheckProductStockResponse;
import com.example.product.dto.ProductStockCheckRequest;
import com.example.product.entity.Product;
import com.example.product.service.ProductService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductStockConsumer {

    private final KafkaTemplate<String, CheckProductStockResponse> kafkaTemplate;
    private final ProductService productService;

    public ProductStockConsumer(
            KafkaTemplate<String, CheckProductStockResponse> kafkaTemplate,
            ProductService productService, ProductService productService1) {

        this.kafkaTemplate = kafkaTemplate;
        this.productService = productService1;
    }

    @KafkaListener(
            topics = "product-stock-check",
            groupId = "product-management-group"
    )
    public CheckProductStockResponse checkStock(ProductStockCheckRequest request) {
        int availableStock = 10;
        boolean available =
                availableStock >= request.quantity();

        System.out.println("this stock check request payload can be " +
                "from the MQ by order service or direct request" +
                " from product service: " + request);

        CheckProductStockResponse response = new CheckProductStockResponse(
                request.orderId(),
                request.productId(),
                request.quantity(),
                availableStock,
                available
        );

        int productInventory = 0;
        boolean responseAvailable = false;
        CheckProductStockResponse productResponse;
        Product product = productService.getProductById(Long.valueOf(request.productId()));
        if (product != null && product.getQuantity() > request.quantity()) {
            productInventory = product.getQuantity();
            responseAvailable = true;
            productResponse = new CheckProductStockResponse(
                    request.orderId(),
                    request.productId(),
                    request.quantity(),
                    productInventory,
                    responseAvailable
            );

        } else {
            productInventory = product != null ? product.getQuantity() : 0;
            productResponse = new CheckProductStockResponse(
                    request.orderId(),
                    request.productId(),
                    request.quantity(),
                    productInventory,
                    responseAvailable
            );
        }

        System.out.println("Received stock response: " + response);

        kafkaTemplate.send(
                "product-stock-response",
                request.orderId(),
//                response
                productResponse
        );

//        return response;
        return productResponse;
    }
}
