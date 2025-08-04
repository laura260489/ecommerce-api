package com.laurarojas.ecommerceapi.exceptions;

public class ApiException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public final Integer statusCode;
    public final String error;

    public ApiException(String message, Integer statusCode, String error) {
        super(message);
        this.statusCode = statusCode;
        this.error = error;
    }
}
