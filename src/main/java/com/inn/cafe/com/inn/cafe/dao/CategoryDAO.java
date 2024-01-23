package com.inn.cafe.com.inn.cafe.dao;

import com.inn.cafe.com.inn.cafe.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryDAO extends JpaRepository<Category, Integer> {

    Optional<Category> findByName(String name);


    @Query(value = "select c from Category c left join Product p on c.id = p.category.id where p.id is not null")
    List<Category> getAllCategory();

    @Query(value = "select c from Category c left join Product p on c.id = p.category.id where p.id is not null")
    List<Category> takeCategory();


}
