package com.hospital.gateway.controller;

import com.hospital.gateway.dto.*;
import com.hospital.gateway.service.HospitalGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gateway")
@CrossOrigin(origins = "*")
public class GatewayController {

    @Autowired
    private HospitalGatewayService gatewayService;

    /**
     * POST /api/gateway/login
     * Reutiliza auth-service para autenticación
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = gatewayService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/gateway/validate/{token}
     * Reutiliza auth-service para validar token
     */
    @GetMapping("/validate/{token}")
    public ResponseEntity<Boolean> validateToken(@PathVariable String token) {
        boolean valid = gatewayService.validateToken(token);
        return ResponseEntity.ok(valid);
    }

    /**
     * POST /api/gateway/register
     * Reutiliza patient-service para registro de pacientes
     */
    @PostMapping("/register")
    public ResponseEntity<Patient> registerPatient(@RequestBody PatientDTO dto) {
        Patient patient = gatewayService.registerPatient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(patient);
    }

    /**
     * GET /api/gateway/patients
     * Obtiene todos los pacientes via patient-service
     */
    @GetMapping("/patients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(gatewayService.getAllPatients());
    }

    /**
     * GET /api/gateway/patients/{id}
     * Obtiene un paciente por ID via patient-service
     */
    @GetMapping("/patients/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(gatewayService.getPatientById(id));
    }

    /**
     * POST /api/gateway/secure-register
     * COMPOSICIÓN SOA: Autentica + Registra + Notifica en una sola operación
     * Demuestra el principio de composición de servicios (Paso 3 del ejercicio)
     *
     * Body JSON:
     * {
     *   "auth": { "username": "admin", "password": "admin123" },
     *   "patient": { "nombre": "...", "email": "...", "telefono": "...", "fechaNacimiento": "1990-01-15" }
     * }
     */
    @PostMapping("/secure-register")
    public ResponseEntity<Patient> secureRegister(@RequestBody SecureRegisterRequest request) {
        Patient patient = gatewayService.secureRegister(
            request.getAuth(),
            request.getPatient()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(patient);
    }
}
