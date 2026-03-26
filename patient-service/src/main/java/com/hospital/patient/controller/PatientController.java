package com.hospital.patient.controller;

import com.hospital.patient.dto.PatientDTO;
import com.hospital.patient.model.Patient;
import com.hospital.patient.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     * POST /api/patients/register
     * Registra un nuevo paciente en el sistema
     */
    @PostMapping("/register")
    public ResponseEntity<Patient> register(@RequestBody PatientDTO dto) {
        Patient patient = patientService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(patient);
    }

    /**
     * GET /api/patients/{id}
     * Obtiene un paciente por su ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.findById(id));
    }

    /**
     * GET /api/patients
     * Retorna todos los pacientes registrados
     */
    @GetMapping
    public ResponseEntity<List<Patient>> getAll() {
        return ResponseEntity.ok(patientService.findAll());
    }

    /**
     * PUT /api/patients/{id}
     * Actualiza un paciente existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Patient> update(@PathVariable Long id, @RequestBody PatientDTO dto) {
        return ResponseEntity.ok(patientService.update(id, dto));
    }

    /**
     * DELETE /api/patients/{id}
     * Elimina un paciente del sistema
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
