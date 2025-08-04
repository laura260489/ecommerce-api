package com.laurarojas.ecommerceapi.controller;

import com.laurarojas.ecommerceapi.dtos.RegisterUserDTO;
import com.laurarojas.ecommerceapi.dtos.ResponseTokenDTO;
import com.laurarojas.ecommerceapi.entity.UserEntity;
import com.laurarojas.ecommerceapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterUserDTO registerUserDTO) {
        userService.registerUser(registerUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED.value()).build();
    }
}
