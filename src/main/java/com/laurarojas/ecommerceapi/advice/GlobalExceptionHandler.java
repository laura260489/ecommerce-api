package com.laurarojas.ecommerceapi.advice;

import com.laurarojas.ecommerceapi.exceptions.ApiException;
import com.laurarojas.ecommerceapi.exceptions.UnauthorizedException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(UnauthorizedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("status_code", ex.statusCode);
        return ResponseEntity.status(ex.statusCode).body(response);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("status_code", ex.statusCode);
        response.put("timestamp", LocalDateTime.now());
        response.put("error", ex.error);
        return ResponseEntity.status(ex.statusCode).body(response);
    }


}
