package com.example.product;

import com.example.product.dto.CheckProductStockResponse;
import com.example.product.dto.ProductStockCheckRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductStockConsumer stockConsumer;

    public ProductController(ProductStockConsumer stockConsumer) {
        this.stockConsumer = stockConsumer;
    }

    @PostMapping
    public ResponseEntity<CheckProductStockResponse> checkStock(
            @RequestBody ProductStockCheckRequest productStockCheckRequest
    ) {
        CheckProductStockResponse productStockRes = stockConsumer.checkStock(productStockCheckRequest);
        System.out.println("product stock check request sent" +
                " - productId: "
                + productStockCheckRequest.productId());
        return ResponseEntity.accepted().body(productStockRes);
    }
}
