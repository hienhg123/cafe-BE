package com.inn.cafe.com.inn.cafe.restImpl;

import com.inn.cafe.com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.com.inn.cafe.model.User;
import com.inn.cafe.com.inn.cafe.service.UserService;
import com.inn.cafe.com.inn.cafe.utils.CafeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@WebMvcTest(UserRestImpl.class)
class UserRestImplTest {

    @Autowired
    private MockMvc mockMvc;

    private final Logger logger = LoggerFactory.getLogger(UserRestImpl.class);

    private CafeUtils input_user;
    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {



    }

    @Test
    void signUp() throws Exception {
        Map<String, String> input_map = new HashMap<>();
        input_map.put("name", "abc");
        input_map.put("contactNumber", "1234567890");
        input_map.put("email", "alibubu@gmail.com");
        input_map.put("password", "1234");

        Mockito.when(userService.signUp(input_map)).thenReturn(CafeUtils.getResponseEntity("Successfully register", HttpStatus.OK));

        mockMvc.perform(post("/user/signup").contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        " \"name\": \"alibubu\",\n" +
                        "\"contactNumber\": \"1234567890\",\n" +
                        "\"email\": \"alibubu@gmail.com\",\n" +
                        "\"password\": \"1234\"\n" +
                        "}")).andExpect(status().isOk());

    }
}