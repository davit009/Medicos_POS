package com.hospital.patient.dto;

import java.time.LocalDate;

public class PatientDTO {
    private String nombre;
    private String email;
    private String telefono;
    private LocalDate fechaNacimiento;

    public PatientDTO() {}

    public PatientDTO(String nombre, String email, String telefono, LocalDate fechaNacimiento) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
}
