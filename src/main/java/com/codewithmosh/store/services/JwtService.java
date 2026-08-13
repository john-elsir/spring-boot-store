package com.codewithmosh.store.services;

import com.codewithmosh.store.config.JwtConfig;
import com.codewithmosh.store.entities.Role;
import com.codewithmosh.store.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@AllArgsConstructor
@Service
public class JwtService {
    private final JwtConfig jwtConfig;

    public Jwt generateAccessToken(User user){
        return generateToken(user, jwtConfig.getAccessTokenExpiration());
    }

    public Jwt generateRefreshToken(User user){
        return generateToken(user, jwtConfig.getRefreshTokenExpiration());
    }

    private Jwt generateToken(User user, long tokenExpiration) {
        var claims = Jwts.claims()
                .subject(user.getId().toString())
                .add("email", user.getEmail())
                .add("name", user.getName())
                .add("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
                .build();


//        return Jwts.builder()
//                .subject(user.getId().toString())
//                .claim("email", user.getEmail())
//                .claim("name", user.getName())
//                .claim("role", user.getRole())
//                .issuedAt(new Date())
//                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
//                .signWith(jwtConfig.getSecreteKey())
//                .compact();

        return new Jwt(claims, jwtConfig.getSecreteKey());
    }

//    public boolean validateToken(String token){
//        try {
//            var claims = getClaims(token);
//            return claims.getExpiration().after(new Date());
//        }catch (JwtException ex){
//            return false;
//        }
//    }

    public Jwt parseToken(String token){
        try {
            var claims = getClaims(token);
            return new Jwt(claims, jwtConfig.getSecreteKey());
        } catch (JwtException e) {
            return null;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecreteKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

//    public Long getUserIdFromToken(String token){
//        return Long.valueOf(getClaims(token).getSubject());
//    }

//    public Role getRoleFromToken(String token){
//        return Role.valueOf(getClaims(token).get("role", String.class));
//    }
}
