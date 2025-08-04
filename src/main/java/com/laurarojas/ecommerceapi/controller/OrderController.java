package com.laurarojas.ecommerceapi.controller;


import com.laurarojas.ecommerceapi.dtos.CreateOrderRequest;
import com.laurarojas.ecommerceapi.dtos.CreateProductRequest;
import com.laurarojas.ecommerceapi.dtos.OrderDTO;
import com.laurarojas.ecommerceapi.dtos.ProductDTO;
import com.laurarojas.ecommerceapi.service.OrderService;
import com.laurarojas.ecommerceapi.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/create-order")
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderDTO created = orderService.createOrder(request);
        return ResponseEntity.ok(created);
    }
}
