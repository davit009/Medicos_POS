package com.hospital.auth.aspect;

import com.hospital.auth.dto.LoginRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ASPECTO: Auditoria de intentos de login
 * 
 * Intercepta el metodo authenticate() de AuthService sin modificarlo.
 * Principio AOP: separacion de preocupaciones transversales (cross-cutting concerns).
 */
@Aspect
@Component
public class AuthLoggingAspect {

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ----------------------------------------------------------
    // Pointcut: cualquier ejecucion del metodo authenticate()
    // ----------------------------------------------------------
    @Pointcut("execution(* com.hospital.auth.service.AuthService.authenticate(..))")
    private void loginPointcut() {}

    @Pointcut("execution(* com.hospital.auth.service.AuthService.validate(..))")
    private void validatePointcut() {}

    // ----------------------------------------------------------
    // BEFORE: Se ejecuta ANTES del login
    // ----------------------------------------------------------
    @Before("loginPointcut()")
    public void logLoginAttempt(JoinPoint jp) {
        LoginRequest req = (LoginRequest) jp.getArgs()[0];
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║  [ASPECTO] INTENTO DE LOGIN DETECTADO            ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.printf( "║  Timestamp : %-35s ║%n", LocalDateTime.now().format(FMT));
        System.out.printf( "║  Usuario   : %-35s ║%n", req.getUsername());
        System.out.printf( "║  Metodo    : %-35s ║%n", jp.getSignature().getName());
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    // ----------------------------------------------------------
    // AFTER RETURNING: Se ejecuta si el login FUE EXITOSO
    // ----------------------------------------------------------
    @AfterReturning(pointcut = "loginPointcut()", returning = "result")
    public void logLoginSuccess(Object result) {
        System.out.println();
        System.out.println("  ✓ [ASPECTO] LOGIN EXITOSO — Token JWT generado");
        System.out.println();
    }

    // ----------------------------------------------------------
    // AFTER THROWING: Se ejecuta si el login FALLO (credenciales invalidas)
    // ----------------------------------------------------------
    @AfterThrowing(pointcut = "loginPointcut()", throwing = "ex")
    public void logLoginFailure(Exception ex) {
        System.out.println();
        System.out.println("  ✗ [ASPECTO] LOGIN FALLIDO — " + ex.getMessage());
        System.out.println("  ⚠ Registrando intento fallido para auditoria de seguridad...");
        System.out.println();
    }

    // ----------------------------------------------------------
    // AROUND: Mide el tiempo de validacion del token
    // ----------------------------------------------------------
    @Around("validatePointcut()")
    public Object measureValidationTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        String token = (String) pjp.getArgs()[0];

        System.out.printf("[ASPECTO] Validando token (primeros 20 chars): %s...%n",
            token.length() > 20 ? token.substring(0, 20) : token);

        Object result = pjp.proceed(); // ejecuta el metodo real

        long duration = System.currentTimeMillis() - start;
        System.out.printf("[ASPECTO] Validacion completada en %d ms | Resultado: %s%n",
            duration, result);

        return result;
    }
}
