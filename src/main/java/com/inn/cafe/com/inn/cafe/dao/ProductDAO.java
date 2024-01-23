package com.inn.cafe.com.inn.cafe.dao;

import com.inn.cafe.com.inn.cafe.model.Product;
import com.inn.cafe.com.inn.cafe.wrapper.ProductWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface ProductDAO extends JpaRepository<Product, Integer> {

    Optional<Product> findByName (String name);


    List<ProductWrapper> getAllProduct();

    @Query("select p from Product p where p.category.id =?1 and p.status = true")
    List<Product> getByCategory(Integer categoryId);

}
