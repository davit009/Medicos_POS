# start-services.ps1
# Levanta los 4 microservicios en ventanas separadas

$base = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host '=========================================' -ForegroundColor Cyan
Write-Host '  HOSPITAL SOA - Iniciando microservicios' -ForegroundColor Cyan
Write-Host '=========================================' -ForegroundColor Cyan

$services = @(
    @{ name = 'auth-service';         port = 8081; dir = 'auth-service' },
    @{ name = 'patient-service';      port = 8082; dir = 'patient-service' },
    @{ name = 'notification-service'; port = 8083; dir = 'notification-service' },
    @{ name = 'hospital-gateway';     port = 8080; dir = 'hospital-gateway' }
)

foreach ($svc in $services) {
    $path = Join-Path $base $svc.dir

    # 1. Intentar usar mvnw.cmd si existe
    $mvnw = Join-Path $path 'mvnw.cmd'
    if (Test-Path $mvnw) {
        Write-Host "Iniciando $($svc.name) en puerto $($svc.port) usando mvnw..." -ForegroundColor Yellow
        Start-Process powershell -ArgumentList '-NoExit', '-Command', "cd '$path'; .\mvnw.cmd spring-boot:run"
        continue
    }

    # 2. Intentar usar Maven local (setup.ps1)
    $localMvn = Join-Path $base ".mvn-local\apache-maven-3.9.6\bin\mvn.cmd"
    if (Test-Path $localMvn) {
        Write-Host "Iniciando $($svc.name) en puerto $($svc.port) usando Maven local..." -ForegroundColor Yellow
        Start-Process powershell -ArgumentList '-NoExit', '-Command', "cd '$path'; & '$localMvn' spring-boot:run"
        continue
    }

    # 3. Fallback a 'mvn' del sistema
    Write-Host "Iniciando $($svc.name) en puerto $($svc.port) usando mvn global..." -ForegroundColor Yellow
    Start-Process powershell -ArgumentList '-NoExit', '-Command', "cd '$path'; mvn spring-boot:run"
}

Write-Host ''
Write-Host 'Todos los servicios iniciados en ventanas separadas.' -ForegroundColor Green
Write-Host 'Espera ~40 segundos y luego ejecuta: .\test-all.ps1' -ForegroundColor Green
Write-Host ''
