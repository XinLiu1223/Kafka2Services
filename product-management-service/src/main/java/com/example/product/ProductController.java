package com.example.product;

import com.example.product.dto.CheckProductStockResponse;
import com.example.product.dto.ProductStockCheckRequest;
import com.example.product.entity.Product;
import com.example.product.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductStockConsumer stockConsumer;
    private final ProductService productService;

    public ProductController(ProductStockConsumer stockConsumer,
                             ProductService productService) {
        this.stockConsumer = stockConsumer;
        this.productService = productService;
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


    // CREATE
    @PostMapping("/management")
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(
            @RequestBody Product product) {

        return productService.createProduct(product);
    }

    // READ ALL
    @GetMapping("/management")
    public List<Product> getAllProducts() {

        return productService.getAllProducts();
    }

    // READ ONE
    @GetMapping("/{id}")
    public Product getProductById(
            @PathVariable Long id) {

        return productService.getProductById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        return productService.updateProduct(
                id,
                product);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);
    }

}
