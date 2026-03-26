package com.hospital.auth.controller;

import com.hospital.auth.dto.AuthResponse;
import com.hospital.auth.dto.LoginRequest;
import com.hospital.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * POST /api/auth/login
     * Autentica el usuario y retorna un JWT
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/auth/validate/{token}
     * Valida un token JWT — reutilizable por cualquier otro servicio
     */
    @GetMapping("/validate/{token}")
    public ResponseEntity<Boolean> validateToken(@PathVariable String token) {
        boolean isValid = authService.validate(token);
        return ResponseEntity.ok(isValid);
    }

    /**
     * GET /api/auth/username/{token}
     * Extrae el username del token
     */
    @GetMapping("/username/{token}")
    public ResponseEntity<String> getUsernameFromToken(@PathVariable String token) {
        String username = authService.getUsernameFromToken(token);
        return ResponseEntity.ok(username);
    }
}
