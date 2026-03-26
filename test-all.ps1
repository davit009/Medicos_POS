# test-all.ps1
# Prueba completa del sistema hospitalario SOA
# Ejecutar DESPUES de start-services.ps1 (esperar ~40 seg)

$ErrorActionPreference = 'Stop'

function Show-Header {
    param([string]$text)
    Write-Host ''
    Write-Host '==========================================' -ForegroundColor Cyan
    Write-Host "  $text" -ForegroundColor Cyan
    Write-Host '==========================================' -ForegroundColor Cyan
}

function Show-Step {
    param([int]$num, [string]$text)
    Write-Host ''
    Write-Host "[$num] $text" -ForegroundColor Yellow
}

function Show-OK {
    param([string]$text)
    Write-Host "    OK $text" -ForegroundColor Green
}

function Show-Fail {
    param([string]$text)
    Write-Host "    FALLO $text" -ForegroundColor Red
}

function Wait-ForService {
    param([string]$url, [string]$name)
    Write-Host "  Esperando $name ..." -NoNewline
    $tries = 0
    while ($tries -lt 20) {
        try {
            Invoke-RestMethod -Uri $url -Method Get -ErrorAction Stop | Out-Null
            Write-Host ' OK' -ForegroundColor Green
            return
        } catch {
            $code = $_.Exception.Response.StatusCode.value__
            if ($code -eq 405 -or $code -eq 404) {
                Write-Host ' OK' -ForegroundColor Green
                return
            }
            Start-Sleep -Seconds 2
            Write-Host -NoNewline '.'
            $tries++
        }
    }
    Write-Host ' TIMEOUT' -ForegroundColor Red
    throw "$name no respondio en $url"
}

# ---- Verificar servicios activos ----
Show-Header 'VERIFICANDO SERVICIOS'
Wait-ForService 'http://localhost:8081/api/auth/validate/test'    'auth-service      (8081)'
Wait-ForService 'http://localhost:8082/api/patients'              'patient-service   (8082)'
Wait-ForService 'http://localhost:8083/api/notifications/send'    'notification-svc  (8083)'
Wait-ForService 'http://localhost:8080/api/gateway/validate/test' 'hospital-gateway  (8080)'
Write-Host ''
Write-Host '  Todos los servicios estan activos.' -ForegroundColor Green

# ============================================================
# PASO 1: AUTH-SERVICE
# ============================================================
Show-Header 'PASO 1 - auth-service (puerto 8081)'

Show-Step 1 'Login con credenciales validas (admin/admin123)'
$token = ''
try {
    $body = '{"username":"admin","password":"admin123"}'
    $authResp = Invoke-RestMethod -Uri 'http://localhost:8081/api/auth/login' -Method Post -ContentType 'application/json' -Body $body
    $token = $authResp.token
    Show-OK "Login exitoso | Usuario: $($authResp.username)"
    Show-OK "Token: $($token.Substring(0, [Math]::Min(40, $token.Length)))..."
} catch {
    Show-Fail "Login fallo: $_"
}

Show-Step 2 'Validar el token JWT'
try {
    $valid = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/validate/$token" -Method Get
    Show-OK "Token valido: $valid"
} catch {
    Show-Fail "Validacion fallo: $_"
}

Show-Step 3 'Login con contrasena INCORRECTA (debe rechazarse)'
try {
    $bad = '{"username":"admin","password":"wrongpassword"}'
    Invoke-RestMethod -Uri 'http://localhost:8081/api/auth/login' -Method Post -ContentType 'application/json' -Body $bad | Out-Null
    Show-Fail 'Debio rechazar las credenciales pero acepto'
} catch {
    Show-OK 'Credenciales invalidas rechazadas correctamente (comportamiento esperado)'
}

# ============================================================
# PASO 2: PATIENT-SERVICE
# ============================================================
Show-Header 'PASO 2 - patient-service (puerto 8082)'

Show-Step 4 'Registrar paciente #1: Juan Perez'
try {
    $p1 = '{"nombre":"Juan Perez","email":"juan@test.com","telefono":"555-0001","fechaNacimiento":"1990-01-15"}'
    $pat1 = Invoke-RestMethod -Uri 'http://localhost:8082/api/patients/register' -Method Post -ContentType 'application/json' -Body $p1
    Show-OK "Registrado | ID: $($pat1.id) | $($pat1.nombre)"
} catch {
    Show-Fail "Registro fallo: $_"
}

Show-Step 5 'Registrar paciente #2: Maria Lopez'
try {
    $p2 = '{"nombre":"Maria Lopez","email":"maria@test.com","telefono":"555-0002","fechaNacimiento":"1985-05-20"}'
    $pat2 = Invoke-RestMethod -Uri 'http://localhost:8082/api/patients/register' -Method Post -ContentType 'application/json' -Body $p2
    Show-OK "Registrado | ID: $($pat2.id) | $($pat2.nombre)"
} catch {
    Show-Fail "Registro fallo: $_"
}

Show-Step 6 'Listar todos los pacientes'
try {
    $all = Invoke-RestMethod -Uri 'http://localhost:8082/api/patients' -Method Get
    Show-OK "Total pacientes: $($all.Count)"
    $all | ForEach-Object { Write-Host "       - ID:$($_.id) | $($_.nombre) | $($_.email)" -ForegroundColor Gray }
} catch {
    Show-Fail "Listar fallo: $_"
}

Show-Step 7 'Obtener paciente por ID=1'
try {
    $byId = Invoke-RestMethod -Uri 'http://localhost:8082/api/patients/1' -Method Get
    Show-OK "Encontrado: $($byId.nombre) | $($byId.email)"
} catch {
    Show-Fail "Busqueda por ID fallo: $_"
}

# ============================================================
# PASO 3: NOTIFICATION-SERVICE
# ============================================================
Show-Header 'PASO 3 - notification-service (puerto 8083)'

Show-Step 8 'Enviar notificacion EMAIL'
try {
    $nBody = '{"destinatario":"paciente@hospital.com","asunto":"Prueba SOA","mensaje":"Bienvenido al sistema","tipo":"EMAIL"}'
    $nResp = Invoke-RestMethod -Uri 'http://localhost:8083/api/notifications/send' -Method Post -ContentType 'application/json' -Body $nBody
    Show-OK "Notificacion enviada | Status: $($nResp.status) | Para: $($nResp.destinatario)"
} catch {
    Show-Fail "Notificacion fallo: $_"
}

Show-Step 9 'Enviar alerta del sistema'
try {
    $alertUrl = 'http://localhost:8083/api/notifications/alert?destinatario=admin@hospital.com' + '&mensaje=Sistema+funcionando+correctamente'
    $aResp = Invoke-RestMethod -Uri $alertUrl -Method Post
    Show-OK "Alerta enviada | Status: $($aResp.status)"
} catch {
    Show-Fail "Alerta fallo: $_"
}

# ============================================================
# PASO 4: HOSPITAL-GATEWAY (Composicion SOA)
# ============================================================
Show-Header 'PASO 4 - hospital-gateway (Composicion SOA - puerto 8080)'

Show-Step 10 'Login VIA GATEWAY (reutiliza auth-service)'
try {
    $gwBody = '{"username":"doctor","password":"doctor123"}'
    $gwAuth = Invoke-RestMethod -Uri 'http://localhost:8080/api/gateway/login' -Method Post -ContentType 'application/json' -Body $gwBody
    Show-OK "Gateway -> auth-service OK | Usuario: $($gwAuth.username)"
} catch {
    Show-Fail "Login via gateway fallo: $_"
}

Show-Step 11 'Listar pacientes VIA GATEWAY (reutiliza patient-service)'
try {
    $gwPats = Invoke-RestMethod -Uri 'http://localhost:8080/api/gateway/patients' -Method Get
    Show-OK "Gateway -> patient-service OK | Pacientes: $($gwPats.Count)"
} catch {
    Show-Fail "Listar via gateway fallo: $_"
}

Show-Step 12 'SECURE-REGISTER: Composicion de 3 servicios en 1 operacion'
Write-Host '    -> Gateway autenticara, registrara paciente y notificara' -ForegroundColor Gray
try {
    $secBody = '{"auth":{"username":"admin","password":"admin123"},"patient":{"nombre":"Carlos Rodriguez","email":"carlos@test.com","telefono":"555-0003","fechaNacimiento":"2000-07-10"}}'
    $secResp = Invoke-RestMethod -Uri 'http://localhost:8080/api/gateway/secure-register' -Method Post -ContentType 'application/json' -Body $secBody
    Show-OK 'COMPOSICION SOA EXITOSA'
    Show-OK "Flujo: auth-service -> patient-service -> notification-service"
    Show-OK "Paciente creado | ID: $($secResp.id) | Nombre: $($secResp.nombre)"
} catch {
    Show-Fail "Secure-register fallo: $_"
}

Show-Step 13 'SECURE-REGISTER FALLIDO (credenciales incorrectas)'
Write-Host '    -> Debe fallar porque el usuario no es valido' -ForegroundColor Gray
try {
    $failBody = '{"auth":{"username":"hacker","password":"123"},"patient":{"nombre":"Intruso","email":"bad@test.com","telefono":"000-0000","fechaNacimiento":"2000-01-01"}}'
    Invoke-RestMethod -Uri 'http://localhost:8080/api/gateway/secure-register' -Method Post -ContentType 'application/json' -Body $failBody | Out-Null
    Show-Fail 'Debio rechazar el registro pero lo acepto'
} catch {
    Show-OK 'REGISTRO RECHAZADO CORRECTAMENTE (Error esperado de autenticacion)'
}

# ============================================================
# RESUMEN
# ============================================================
Show-Header 'RESUMEN FINAL'
Write-Host ''
Write-Host '  Servicios probados:' -ForegroundColor White
Write-Host '    [8081] auth-service         - LOGIN y VALIDACION JWT' -ForegroundColor Green
Write-Host '    [8082] patient-service      - CRUD de pacientes (H2)' -ForegroundColor Green
Write-Host '    [8083] notification-service - ENVIO de notificaciones' -ForegroundColor Green
Write-Host '    [8080] hospital-gateway     - COMPOSICION SOA (3 servicios)' -ForegroundColor Green
Write-Host ''
Write-Host '  Principios SOA demostrados:' -ForegroundColor White
Write-Host '    [+] Reutilizacion    - Cada servicio es independiente y reutilizable' -ForegroundColor Green
Write-Host '    [+] Bajo acoplamiento - Comunicacion solo por HTTP REST' -ForegroundColor Green
Write-Host '    [+] Abstraccion      - Gateway oculta complejidad interna' -ForegroundColor Green
Write-Host '    [+] Composicion      - secure-register: 3 servicios en 1 operacion' -ForegroundColor Green
Write-Host ''
