package com.inn.cafe.com.inn.cafe.restImpl;

import com.inn.cafe.com.inn.cafe.rest.DashBoardRest;
import com.inn.cafe.com.inn.cafe.service.DashBoardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
public class DashBoardRestImpl implements DashBoardRest {

    private final DashBoardService dashBoardService;

    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        return dashBoardService.getCount();
    }
}
