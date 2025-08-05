package com.laurarojas.ecommerceapi.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(String id, String email, String firstName, String lastName,
                                List<String> role, boolean frequentUser) {
        return JWT.create()
                .withSubject(email)
                .withClaim("role", role)
                .withClaim("user_id", id)
                .withClaim("first_name", firstName)
                .withClaim("last_name", lastName)
                .withClaim("frequent_user", frequentUser)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 43200000))
                .sign(Algorithm.HMAC256(secret));
    }
}
