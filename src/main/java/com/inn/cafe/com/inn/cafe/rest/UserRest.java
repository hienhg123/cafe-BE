package com.inn.cafe.com.inn.cafe.rest;

import com.inn.cafe.com.inn.cafe.model.User;
import com.inn.cafe.com.inn.cafe.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = "/user")
public interface UserRest {

    @PostMapping(path = "/signup")
    public ResponseEntity<String> signUp(@RequestBody Map<String, String> requestMap);

    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> requestMap);

    @GetMapping(path = "/getAllUser")
    public ResponseEntity<List<User>> getAllUser();

    @PostMapping(path = "/updateUser")
    public ResponseEntity<String> updateUser(@RequestBody Map<String, String> requestMap);

    @GetMapping(path = "/checkToken")
    public ResponseEntity<String> checkToken();

    @PostMapping(path = "/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody Map<String,String> requestMap);

    @PostMapping(path = "/forgotPassword")
    public ResponseEntity<String> forgetPassword(@RequestBody Map<String,String> requestMap);

}





