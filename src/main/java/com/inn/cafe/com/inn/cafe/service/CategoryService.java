package com.inn.cafe.com.inn.cafe.service;

import com.inn.cafe.com.inn.cafe.model.Category;
import com.inn.cafe.com.inn.cafe.model.Product;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    ResponseEntity<String> addNewCategory(Map<String, String> requestMap);

    ResponseEntity<List<Category>> getAllCategory(String filterValue);

    ResponseEntity<String> updateCategory(Map<String, String> requestMap);

}
