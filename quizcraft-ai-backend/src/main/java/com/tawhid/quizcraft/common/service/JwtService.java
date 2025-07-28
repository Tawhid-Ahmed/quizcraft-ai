package com.tawhid.quizcraft.common.service;

import com.tawhid.quizcraft.auth.entity.User;
import com.tawhid.quizcraft.common.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtConfig jwtConfig;

    public String generateToken(User user){
        var roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .toList();
                
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());
        
        return Jwts.builder()
                .setId(String.valueOf(System.nanoTime())) // Unique token ID
                .setSubject(user.getEmail())
                .claim("role", roles)
                .claim("userId", user.getId().toString())
                .claim("timestamp", System.currentTimeMillis()) // Add current timestamp
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtConfig.getSigningKey(), SignatureAlgorithm.HS256)
                .compact();


    }

    public String extractEmail(String token){
        return getClaims(token).getSubject();
    }

    public String extractUsername(String token){
        return getClaims(token).get("email").toString();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractEmail(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));

    }

    private boolean isTokenExpired(String token){
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(jwtConfig.getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
