package com.laurarojas.ecommerceapi.controller;

import com.laurarojas.ecommerceapi.dtos.ResponseMessageDTO;
import com.laurarojas.ecommerceapi.dtos.UserUpdateDTO;
import com.laurarojas.ecommerceapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/administration/get-user/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/administration/list-users")
    public ResponseEntity<?> getListUsers() {
        return ResponseEntity.ok(userService.getListUsers());
    }

    @DeleteMapping("/administration/delete-user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        ResponseMessageDTO response = userService.deleteUserById(id);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/administration/update-user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody UserUpdateDTO userUpdateDTO) {
        ResponseMessageDTO response = userService.updateUserById(id, userUpdateDTO);
        return ResponseEntity.ok().body(response);
    }

}
