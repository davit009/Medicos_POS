package com.hospital.patient.aspect;

import com.hospital.patient.dto.PatientDTO;
import com.hospital.patient.model.Patient;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ASPECTO: Auditoria de operaciones sobre pacientes
 *
 * Intercepta las operaciones de PatientService para auditoría
 * sin modificar la lógica de negocio.
 */
@Aspect
@Component
public class PatientAuditAspect {

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ----------------------------------------------------------
    // Pointcuts
    // ----------------------------------------------------------
    @Pointcut("execution(* com.hospital.patient.service.PatientService.register(..))")
    private void registerPointcut() {}

    @Pointcut("execution(* com.hospital.patient.service.PatientService.findById(..))")
    private void findByIdPointcut() {}

    @Pointcut("execution(* com.hospital.patient.service.PatientService.delete(..))")
    private void deletePointcut() {}

    // ----------------------------------------------------------
    // BEFORE: Log antes de registrar un nuevo paciente
    // ----------------------------------------------------------
    @Before("registerPointcut()")
    public void logRegisterAttempt(JoinPoint jp) {
        PatientDTO dto = (PatientDTO) jp.getArgs()[0];
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║  [ASPECTO] SOLICITUD DE REGISTRO DE PACIENTE     ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.printf( "║  Timestamp : %-35s ║%n", LocalDateTime.now().format(FMT));
        System.out.printf( "║  Nombre    : %-35s ║%n", dto.getNombre());
        System.out.printf( "║  Email     : %-35s ║%n", dto.getEmail());
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    // ----------------------------------------------------------
    // AFTER RETURNING: Log cuando el registro es exitoso
    // ----------------------------------------------------------
    @AfterReturning(pointcut = "registerPointcut()", returning = "patient")
    public void logRegisterSuccess(Patient patient) {
        System.out.printf("  ✓ [ASPECTO] Paciente REGISTRADO exitosamente | ID asignado: %d%n%n",
            patient.getId());
    }

    // ----------------------------------------------------------
    // AFTER THROWING: Log cuando el registro falla (email duplicado, etc.)
    // ----------------------------------------------------------
    @AfterThrowing(pointcut = "registerPointcut()", throwing = "ex")
    public void logRegisterFailure(Exception ex) {
        System.out.println("  ✗ [ASPECTO] REGISTRO FALLIDO — " + ex.getMessage());
        System.out.println();
    }

    // ----------------------------------------------------------
    // AROUND: Mide el tiempo de busqueda por ID y detecta si no existe
    // ----------------------------------------------------------
    @Around("findByIdPointcut()")
    public Object auditFindById(ProceedingJoinPoint pjp) throws Throwable {
        Long id = (Long) pjp.getArgs()[0];
        System.out.printf("[ASPECTO] Consulta de paciente ID=%d iniciada%n", id);

        long start = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();
            long ms = System.currentTimeMillis() - start;
            System.out.printf("[ASPECTO] Consulta ID=%d completada en %d ms%n", id, ms);
            return result;
        } catch (Exception ex) {
            System.out.printf("[ASPECTO] Consulta ID=%d — paciente NO encontrado%n", id);
            throw ex;
        }
    }

    // ----------------------------------------------------------
    // BEFORE: Alerta antes de eliminar un paciente
    // ----------------------------------------------------------
    @Before("deletePointcut()")
    public void warnBeforeDelete(JoinPoint jp) {
        Long id = (Long) jp.getArgs()[0];
        System.out.printf("[ASPECTO] ⚠ ADVERTENCIA: Eliminando paciente ID=%d — %s%n",
            id, LocalDateTime.now().format(FMT));
    }
}
