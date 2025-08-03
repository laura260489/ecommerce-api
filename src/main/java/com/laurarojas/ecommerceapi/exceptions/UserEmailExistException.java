package com.laurarojas.ecommerceapi.exceptions;

public class UserEmailExistException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final Integer statusCode;

    public UserEmailExistException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
