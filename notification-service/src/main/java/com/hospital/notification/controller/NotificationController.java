package com.hospital.notification.controller;

import com.hospital.notification.dto.NotificationRequest;
import com.hospital.notification.dto.NotificationResponse;
import com.hospital.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * POST /api/notifications/send
     * Envía una notificación (email, SMS o alerta)
     */
    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> send(@RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.send(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/notifications/alert
     * Envía una alerta rápida del sistema
     */
    @PostMapping("/alert")
    public ResponseEntity<NotificationResponse> sendAlert(
            @RequestParam String destinatario,
            @RequestParam String mensaje) {
        NotificationResponse response = notificationService.sendAlert(destinatario, mensaje);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/notifications/patient-registered
     * Notifica el registro exitoso de un paciente
     */
    @PostMapping("/patient-registered")
    public ResponseEntity<NotificationResponse> notifyPatientRegistration(
            @RequestParam String nombre,
            @RequestParam String email) {
        NotificationResponse response = notificationService.notifyPatientRegistration(nombre, email);
        return ResponseEntity.ok(response);
    }
}
