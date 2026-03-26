package com.hospital.notification.service;

import com.hospital.notification.dto.NotificationRequest;
import com.hospital.notification.dto.NotificationResponse;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    /**
     * Simula el envío de una notificación — Servicio reutilizable SOA
     * En producción, usaría JavaMail, Twilio, Firebase, etc.
     */
    public NotificationResponse send(NotificationRequest request) {
        // Simulación: en producción se conectaría a un servidor SMTP, SMS API, etc.
        System.out.println("=================================================================");
        System.out.println("[NOTIFICATION-SERVICE] Enviando notificación...");
        System.out.println("  Tipo:         " + request.getTipo());
        System.out.println("  Destinatario: " + request.getDestinatario());
        System.out.println("  Asunto:       " + request.getAsunto());
        System.out.println("  Mensaje:      " + request.getMensaje());
        System.out.println("=================================================================");

        return new NotificationResponse(
            "ENVIADA",
            "Notificación enviada exitosamente a " + request.getDestinatario(),
            request.getDestinatario()
        );
    }

    /**
     * Envía una alerta del sistema — Composición a nivel de servicio
     */
    public NotificationResponse sendAlert(String destinatario, String mensaje) {
        NotificationRequest request = new NotificationRequest(
            destinatario,
            "Alerta del Sistema Hospitalario",
            mensaje,
            "ALERT"
        );
        return send(request);
    }

    /**
     * Notifica el registro de un nuevo paciente
     */
    public NotificationResponse notifyPatientRegistration(String patientName, String email) {
        NotificationRequest request = new NotificationRequest(
            email,
            "Bienvenido al Sistema Hospitalario",
            "Estimado/a " + patientName + ", su registro ha sido exitoso en nuestro sistema. "
                + "A partir de ahora puede acceder a todos nuestros servicios hospitalarios.",
            "EMAIL"
        );
        return send(request);
    }
}
