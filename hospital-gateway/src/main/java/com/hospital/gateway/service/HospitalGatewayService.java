package com.hospital.gateway.service;

import com.hospital.gateway.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class HospitalGatewayService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${services.auth.url}")
    private String authUrl;

    @Value("${services.patient.url}")
    private String patientUrl;

    @Value("${services.notification.url}")
    private String notificationUrl;

    // =====================================================================
    // AUTH SERVICE — Reutiliza el servicio de autenticación SOA
    // =====================================================================

    /**
     * Delega el login al auth-service vía HTTP
     */
    public AuthResponse login(LoginRequest req) {
        return restTemplate.postForObject(
            authUrl + "/login", req, AuthResponse.class);
    }

    /**
     * Delega la validación de token al auth-service
     */
    public boolean validateToken(String token) {
        Boolean result = restTemplate.getForObject(
            authUrl + "/validate/" + token, Boolean.class);
        return result != null && result;
    }

    // =====================================================================
    // PATIENT SERVICE — Reutiliza el servicio de registro SOA
    // =====================================================================

    /**
     * Registra un paciente llamando al patient-service vía HTTP
     */
    public Patient registerPatient(PatientDTO dto) {
        return restTemplate.postForObject(
            patientUrl + "/register", dto, Patient.class);
    }

    /**
     * Obtiene todos los pacientes del patient-service
     */
    public List<Patient> getAllPatients() {
        Patient[] patients = restTemplate.getForObject(
            patientUrl, Patient[].class);
        return patients != null ? List.of(patients) : List.of();
    }

    /**
     * Obtiene un paciente por ID del patient-service
     */
    public Patient getPatientById(Long id) {
        return restTemplate.getForObject(
            patientUrl + "/" + id, Patient.class);
    }

    // =====================================================================
    // NOTIFICATION SERVICE — Reutiliza el servicio de notificaciones SOA
    // =====================================================================

    /**
     * Notifica el registro de un paciente a través del notification-service
     */
    public void notifyRegistration(String nombre, String email) {
        String url = notificationUrl + "/patient-registered?nombre=" + nombre + "&email=" + email;
        restTemplate.postForObject(url, null, Object.class);
    }

    // =====================================================================
    // COMPOSICIÓN SOA — Combina múltiples servicios en un flujo de negocio
    // =====================================================================

    /**
     * secureRegister: Composición de 3 servicios
     *   1. Autentica al usuario con auth-service
     *   2. Si la auth es exitosa, registra el paciente en patient-service
     *   3. Envía notificación de bienvenida via notification-service
     *
     * Este es el principio clave de SOA: composición de servicios reutilizables
     */
    public Patient secureRegister(LoginRequest auth, PatientDTO patientData) {
        // Paso 1: Autenticar — Principio SOA de reutilización
        System.out.println("[GATEWAY] Paso 1: Autenticando usuario '" + auth.getUsername() + "'...");
        login(auth); // lanza excepcion si las credenciales son invalidas
        System.out.println("[GATEWAY] ✓ Autenticación exitosa. Token generado.");

        // Paso 2: Registrar Paciente — Reutiliza patient-service
        System.out.println("[GATEWAY] Paso 2: Registrando paciente '" + patientData.getNombre() + "'...");
        Patient registeredPatient = registerPatient(patientData);
        System.out.println("[GATEWAY] ✓ Paciente registrado con ID: " + registeredPatient.getId());

        // Paso 3: Notificar — Reutiliza notification-service (composición)
        System.out.println("[GATEWAY] Paso 3: Enviando notificación de bienvenida...");
        notifyRegistration(registeredPatient.getNombre(), registeredPatient.getEmail());
        System.out.println("[GATEWAY] ✓ Notificación enviada exitosamente.");
        System.out.println("[GATEWAY] Registro seguro completado: SOA Composition Flow exitoso.");

        return registeredPatient;
    }
}
