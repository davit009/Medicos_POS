package com.hospital.notification.dto;

import java.time.LocalDateTime;

public class NotificationResponse {
    private String status;
    private String mensaje;
    private String destinatario;
    private LocalDateTime timestamp;

    public NotificationResponse() {}

    public NotificationResponse(String status, String mensaje, String destinatario) {
        this.status = status;
        this.mensaje = mensaje;
        this.destinatario = destinatario;
        this.timestamp = LocalDateTime.now();
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
