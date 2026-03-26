package com.hospital.patient.service;

import com.hospital.patient.dto.PatientDTO;
import com.hospital.patient.model.Patient;
import com.hospital.patient.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    /**
     * Registra un nuevo paciente — Servicio reutilizable SOA
     * Consumido por patient-service directamente y por hospital-gateway vía HTTP
     */
    public Patient register(PatientDTO dto) {
        if (patientRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Ya existe un paciente con email: " + dto.getEmail());
        }

        Patient patient = new Patient(
            dto.getNombre(),
            dto.getEmail(),
            dto.getTelefono(),
            dto.getFechaNacimiento()
        );

        return patientRepository.save(patient);
    }

    /**
     * Busca un paciente por ID
     */
    public Patient findById(Long id) {
        return patientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + id));
    }

    /**
     * Retorna todos los pacientes registrados
     */
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    /**
     * Actualiza los datos de un paciente
     */
    public Patient update(Long id, PatientDTO dto) {
        Patient patient = findById(id);
        patient.setNombre(dto.getNombre());
        patient.setTelefono(dto.getTelefono());
        patient.setFechaNacimiento(dto.getFechaNacimiento());
        return patientRepository.save(patient);
    }

    /**
     * Elimina un paciente por ID
     */
    public void delete(Long id) {
        Patient patient = findById(id);
        patientRepository.delete(patient);
    }
}
