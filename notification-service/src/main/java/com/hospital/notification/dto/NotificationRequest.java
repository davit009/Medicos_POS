package com.hospital.notification.dto;

public class NotificationRequest {
    private String destinatario;
    private String asunto;
    private String mensaje;
    private String tipo; // EMAIL, SMS, ALERT

    public NotificationRequest() {}

    public NotificationRequest(String destinatario, String asunto, String mensaje, String tipo) {
        this.destinatario = destinatario;
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.tipo = tipo;
    }

    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
