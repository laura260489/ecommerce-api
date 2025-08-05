package com.laurarojas.ecommerceapi.controller;

import com.laurarojas.ecommerceapi.dtos.LoginDTO;
import com.laurarojas.ecommerceapi.dtos.ResponseTokenDTO;
import com.laurarojas.ecommerceapi.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request) {
        ResponseTokenDTO responseTokenDTO = authService.validateLogin(request);
        return ResponseEntity.ok(responseTokenDTO);
    }
}
