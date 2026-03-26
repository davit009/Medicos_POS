package com.hospital.auth.service;

import com.hospital.auth.dto.AuthResponse;
import com.hospital.auth.dto.LoginRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // Usuarios en memoria para demostración SOA
    // En producción, se usaría una base de datos
    private static final Map<String, String> USERS = new HashMap<>();

    static {
        USERS.put("admin", "admin123");
        USERS.put("doctor", "doctor123");
        USERS.put("enfermera", "enfermera123");
    }

    /**
     * Autentica un usuario y genera un JWT — Principio SOA: servicio reutilizable
     */
    public AuthResponse authenticate(LoginRequest request) {
        String storedPassword = USERS.get(request.getUsername());

        if (storedPassword == null || !storedPassword.equals(request.getPassword())) {
            throw new RuntimeException("Credenciales invalidas para usuario: " + request.getUsername());
        }

        String token = generateToken(request.getUsername());
        return new AuthResponse(token, "Login exitoso", request.getUsername());
    }

    /**
     * Valida un token JWT — consumido por el Gateway y otros servicios
     */
    public boolean validate(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrae el nombre de usuario de un token
     */
    public String getUsernameFromToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo extraer el usuario del token");
        }
    }

    private String generateToken(String username) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim("role", "USER")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
