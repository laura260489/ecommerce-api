package com.laurarojas.ecommerceapi.controller;

import com.laurarojas.ecommerceapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/administration/list-users")
    public ResponseEntity<?> getListUsers() {
        return ResponseEntity.ok(userService.getListUsers());
    }

}
