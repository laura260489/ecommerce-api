package com.laurarojas.ecommerceapi.exceptions;

public class UnauthorizedException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  public final Integer statusCode;

  public UnauthorizedException(String message, Integer statusCode) {
    super(message);
    this.statusCode = statusCode;
  }
}
