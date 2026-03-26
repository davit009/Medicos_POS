package com.hospital.gateway.dto;

public class SecureRegisterRequest {
    private LoginRequest auth;
    private PatientDTO patient;

    public SecureRegisterRequest() {}

    public LoginRequest getAuth() { return auth; }
    public void setAuth(LoginRequest auth) { this.auth = auth; }
    public PatientDTO getPatient() { return patient; }
    public void setPatient(PatientDTO patient) { this.patient = patient; }
}
