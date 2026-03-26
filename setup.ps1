# setup.ps1
# Descarga Maven localmente para este proyecto (solo se necesita correr UNA vez)

$base     = Split-Path -Parent $MyInvocation.MyCommand.Path
$mvnDir   = Join-Path $base '.mvn-local'
$mvnVer   = 'apache-maven-3.9.6'
$mvnZip   = Join-Path $base "$mvnVer-bin.zip"
$mvnHome  = Join-Path $mvnDir $mvnVer
$mvnExe   = Join-Path $mvnHome 'bin\mvn.cmd'
$mvnUrl   = "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/$mvnVer-bin.zip"

Write-Host '==========================================' -ForegroundColor Cyan
Write-Host '  HOSPITAL SOA - Setup de Maven local' -ForegroundColor Cyan
Write-Host '==========================================' -ForegroundColor Cyan

if (Test-Path $mvnExe) {
    Write-Host "  Maven ya esta instalado en: $mvnHome" -ForegroundColor Green
} else {
    Write-Host "  Descargando Maven $mvnVer ..." -ForegroundColor Yellow
    Write-Host "  (puede tardar 1-2 minutos segun tu conexion)" -ForegroundColor Gray
    Invoke-WebRequest -Uri $mvnUrl -OutFile $mvnZip -UseBasicParsing
    Write-Host '  Extrayendo...' -ForegroundColor Yellow
    Expand-Archive -Path $mvnZip -DestinationPath $mvnDir -Force
    Remove-Item $mvnZip -Force
    Write-Host "  Maven listo en: $mvnHome" -ForegroundColor Green
}

Write-Host ''
Write-Host '  Ahora puedes correr: .\start-services.ps1' -ForegroundColor Green
Write-Host ''
