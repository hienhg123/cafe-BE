package com.inn.cafe.com.inn.cafe.rest;


import com.inn.cafe.com.inn.cafe.model.Product;
import com.inn.cafe.com.inn.cafe.wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin("http://localhost:4200")
@RequestMapping(path = "/product")
public interface ProductRest {

    @PostMapping(path = "/add")
    public ResponseEntity<String> addNewProduct(@RequestBody Map<String, String> requestMap);

    @GetMapping(path = "/get")
    public ResponseEntity<List<ProductWrapper>> getAllProduct();

    @PostMapping(path = "/update")
    public ResponseEntity<String> updateProduct(@RequestBody Map<String, String> requestMap);

    @PostMapping(path = "/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer id);

    @PostMapping(path = "/updateStatus")
    public ResponseEntity<String> updateProductStatus(@RequestBody Map<String, String> requestMap);

    @GetMapping(path = "/getByCategory/{categoryId}")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable Integer categoryId);

    @GetMapping(path = "/getById/{id}")
    public ResponseEntity<Product> getById(@PathVariable Integer id);
}
