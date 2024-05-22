package com.berkayg.redis.controller;

import com.berkayg.redis.domain.Product;
import com.berkayg.redis.repository.ProductRepository;
import com.berkayg.redis.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public Product save(@RequestBody Product product){
        return productService.save(product);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id){
        productService.delete(id);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable String id, @RequestBody Product product){
        product.setId(id);
        return productService.update(product);
    }

    @GetMapping("/{id}")
    public Product findById(@PathVariable String id){
        return productService.findById(id);
    }

    @GetMapping
    public List<Product> getAllProducts(){
        return productService.getAllProducts();
    }
}