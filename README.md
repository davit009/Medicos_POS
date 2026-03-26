# 🏥 Sistema de Gestión Hospitalario — SOA con Spring Boot

Un sistema hospitalario implementando **Arquitectura Orientada a Servicios (SOA)** con 4 microservicios Spring Boot independientes.

## Arquitectura

```
hospital-gateway  (Puerto 8080)  ← API Gateway (composición)
   │
   ├── auth-service          (Puerto 8081)  ← JWT Login/Validación
   ├── patient-service       (Puerto 8082)  ← CRUD Pacientes (H2)
   └── notification-service  (Puerto 8083)  ← Notificaciones/Alertas
```

## Principios SOA Implementados

| Principio       | Implementación                                                            |
|-----------------|---------------------------------------------------------------------------|
| **Reutilización** | Cada servicio expone contratos REST reutilizables por cualquier cliente  |
| **Bajo acoplamiento** | Proyectos independientes, se comunican solo por HTTP                |
| **Abstracción** | El gateway oculta la complejidad interna de cada microservicio             |
| **Composición** | `secureRegister` encadena auth + registro + notificación en 1 operación   |

---

## 🚀 Automatización y Pruebas

Para facilitar la revisión y ejecución del proyecto, se han incluido scripts de PowerShell que automatizan todo el proceso:

1.  **`setup.ps1`**: Descarga automáticamente una instancia local de **Apache Maven 3.9.6** dentro de la carpeta `.mvn-local`. Esto permite que el proyecto se pueda compilar y ejecutar incluso si no tienes Maven instalado globalmente en tu sistema.
2.  **`start-services.ps1`**: Inicia automáticamente los 4 microservicios en ventanas de terminal independientes. Detecta el Maven local instalado por el setup y arranca cada servicio en su puerto correspondiente.
3.  **`test-all.ps1`**: **Script principal de validación**. Realiza una prueba integral de todo el sistema SOA:
    *   **Verificación de Salud**: Espera a que los 4 servicios respondan antes de iniciar.
    *   **Paso 1 (Auth)**: Prueba el login con credenciales válidas e inválidas y valida la generación de JWT.
    *   **Paso 2 (Patients)**: Registra dos pacientes directamente en el microservicio de pacientes y lista los resultados.
    *   **Paso 3 (Notification)**: Prueba el envío de correos y alertas del sistema.
    *   **Paso 4 (Gateway - Composición SOA)**: Realiza la prueba más importante, el `secure-register`. Este endpoint del Gateway demuestra la **composición de servicios** al llamar internamente a AuthService, PatientService y NotificationService en una sola operación atómica.

---

## Instrucciones de Ejecución

### Requisitos
- **Java 17+** (o Java 21)
- **Maven 3.8+**

### Paso 1 — Compilar todos los proyectos

```bash
cd auth-service         && mvn clean package -DskipTests && cd ..
cd patient-service      && mvn clean package -DskipTests && cd ..
cd notification-service && mvn clean package -DskipTests && cd ..
cd hospital-gateway     && mvn clean package -DskipTests && cd ..
```

### Paso 2 — Iniciar los servicios (4 terminales separadas)

```bash
# Terminal 1 — auth-service
cd auth-service
mvn spring-boot:run

# Terminal 2 — patient-service
cd patient-service
mvn spring-boot:run

# Terminal 3 — notification-service
cd notification-service
mvn spring-boot:run

# Terminal 4 — hospital-gateway
cd hospital-gateway
mvn spring-boot:run
```

> ⚠️ Iniciar **primero** los 3 servicios (8081, 8082, 8083) y luego el gateway (8080).

---

## 🧪 Pruebas con cURL

### Usuarios de Prueba (auth-service)
| Username | Password |
|---|---|
| `admin` | `admin123` |
| `doctor` | `doctor123` |
| `enfermera` | `enfermera123` |

---

### Paso 1: Probar auth-service directamente (puerto 8081)

**Login:**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```
**Respuesta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Login exitoso",
  "username": "admin"
}
```

**Validar token:**
```bash
curl http://localhost:8081/api/auth/validate/TU_TOKEN_AQUI
# Respuesta: true o false
```

---

### Paso 2: Probar patient-service directamente (puerto 8082)

**Registrar paciente:**
```bash
curl -X POST http://localhost:8082/api/patients/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Perez",
    "email": "juan@test.com",
    "telefono": "555-0001",
    "fechaNacimiento": "1990-01-15"
  }'
```
**Respuesta esperada:**
```json
{
  "id": 1,
  "nombre": "Juan Perez",
  "email": "juan@test.com",
  "telefono": "555-0001",
  "fechaNacimiento": "1990-01-15",
  "fechaRegistro": "2024-01-01T10:00:00"
}
```

**Listar todos los pacientes:**
```bash
curl http://localhost:8082/api/patients
```

**Obtener paciente por ID:**
```bash
curl http://localhost:8082/api/patients/1
```

---

### Paso 3: Probar notification-service directamente (puerto 8083)

**Enviar notificación:**
```bash
curl -X POST http://localhost:8083/api/notifications/send \
  -H "Content-Type: application/json" \
  -d '{
    "destinatario": "paciente@hospital.com",
    "asunto": "Prueba SOA",
    "mensaje": "Notificación de prueba",
    "tipo": "EMAIL"
  }'
```

---

### Paso 4: Probar el Gateway — Composición SOA (puerto 8080)

**Login via Gateway:**
```bash
curl -X POST http://localhost:8080/api/gateway/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**Registro via Gateway:**
```bash
curl -X POST http://localhost:8080/api/gateway/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Maria Lopez",
    "email": "maria@test.com",
    "telefono": "555-0002",
    "fechaNacimiento": "1985-05-20"
  }'
```

**🚀 SECURE-REGISTER: Composición de 3 Servicios en 1 Operación:**
```bash
curl -X POST http://localhost:8080/api/gateway/secure-register \
  -H "Content-Type: application/json" \
  -d '{
    "auth": {
      "username": "admin",
      "password": "admin123"
    },
    "patient": {
      "nombre": "Carlos Rodriguez",
      "email": "carlos@test.com",
      "telefono": "555-0003",
      "fechaNacimiento": "2000-07-10"
    }
  }'
```
Este endpoint demuestra la **composición SOA**: internamente llama a:
1. `auth-service` → autentica al usuario
2. `patient-service` → registra el paciente
3. `notification-service` → envía notificación de bienvenida

**Listar pacientes via Gateway:**
```bash
curl http://localhost:8080/api/gateway/patients
```

---

## 📁 Estructura de Archivos

```
medic/
├── auth-service/
│   ├── pom.xml
│   └── src/main/java/com/hospital/auth/
│       ├── AuthServiceApplication.java
│       ├── config/SecurityConfig.java
│       ├── controller/AuthController.java
│       ├── dto/LoginRequest.java
│       ├── dto/AuthResponse.java
│       └── service/AuthService.java
│
├── patient-service/
│   ├── pom.xml
│   └── src/main/java/com/hospital/patient/
│       ├── PatientServiceApplication.java
│       ├── model/Patient.java
│       ├── dto/PatientDTO.java
│       ├── repository/PatientRepository.java
│       ├── service/PatientService.java
│       └── controller/PatientController.java
│
├── notification-service/
│   ├── pom.xml
│   └── src/main/java/com/hospital/notification/
│       ├── NotificationServiceApplication.java
│       ├── dto/NotificationRequest.java
│       ├── dto/NotificationResponse.java
│       ├── service/NotificationService.java
│       └── controller/NotificationController.java
│
└── hospital-gateway/
    ├── pom.xml
    └── src/main/java/com/hospital/gateway/
        ├── HospitalGatewayApplication.java
        ├── config/RestTemplateConfig.java
        ├── dto/ (LoginRequest, AuthResponse, Patient, PatientDTO, SecureRegisterRequest)
        ├── service/HospitalGatewayService.java
        └── controller/GatewayController.java
```

---

## Endpoints Resumen

### auth-service (8081)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/login` | Autentica y genera JWT |
| GET | `/api/auth/validate/{token}` | Valida un token |
| GET | `/api/auth/username/{token}` | Extrae username del token |

### patient-service (8082)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/patients/register` | Registra paciente (H2) |
| GET | `/api/patients` | Lista todos los pacientes |
| GET | `/api/patients/{id}` | Obtiene paciente por ID |
| PUT | `/api/patients/{id}` | Actualiza paciente |
| DELETE | `/api/patients/{id}` | Elimina paciente |

### notification-service (8083)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/notifications/send` | Envía notificación |
| POST | `/api/notifications/alert` | Envía alerta rápida |
| POST | `/api/notifications/patient-registered` | Notifica registro |

### hospital-gateway (8080)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/gateway/login` | Login via gateway |
| GET | `/api/gateway/validate/{token}` | Valida token via gateway |
| POST | `/api/gateway/register` | Registra paciente via gateway |
| GET | `/api/gateway/patients` | Lista pacientes via gateway |
| GET | `/api/gateway/patients/{id}` | Paciente por ID via gateway |
| POST | `/api/gateway/secure-register` | **Composición SOA**: auth + registro + notificación |
