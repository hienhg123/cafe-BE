package com.inn.cafe.com.inn.cafe.serviceImpl;

import com.inn.cafe.com.inn.cafe.dao.BillDAO;
import com.inn.cafe.com.inn.cafe.dao.CategoryDAO;
import com.inn.cafe.com.inn.cafe.dao.ProductDAO;
import com.inn.cafe.com.inn.cafe.service.DashBoardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class DashBoardServiceImpl implements DashBoardService {

    private final CategoryDAO categoryDAO;

    private final ProductDAO productDAO;

    private final BillDAO billDAO;

    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        Map<String, Object> map = new HashMap<>();
        map.put("category: ",categoryDAO.count());
        map.put("product: ",productDAO.count());
        map.put("bill: ",billDAO.count());
        return new ResponseEntity(map, HttpStatus.OK);
    }
}
