package com.inn.cafe.com.inn.cafe.dao;

import com.inn.cafe.com.inn.cafe.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BillDAO extends JpaRepository<Bill, Integer> {
    List<Bill> findByCreatedByOrderById(String currentUser);

    @Query(value = "select b from Bill b order by b.id DESC ")
    List<Bill> getAllBills();
}
