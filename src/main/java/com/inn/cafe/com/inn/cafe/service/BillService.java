package com.inn.cafe.com.inn.cafe.service;

import com.inn.cafe.com.inn.cafe.model.Bill;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface BillService {
    ResponseEntity<String> generateReport(Map<String, Object> requestMap);

    ResponseEntity<List<Bill>> getAllBill();

    ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap);
}
