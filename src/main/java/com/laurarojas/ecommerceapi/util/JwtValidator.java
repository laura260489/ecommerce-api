package com.laurarojas.ecommerceapi.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtValidator {

    @Value("${jwt.secret}")
    private String secret;

    public DecodedJWT validateToken(String token) {
        return JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token);
    }

    public String getEmailFromToken(String token) {
        return validateToken(token).getSubject();
    }
}
