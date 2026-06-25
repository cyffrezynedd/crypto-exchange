param(
    [string]$User = $env:POSTGRES_USER,
    [string]$Db = $env:POSTGRES_DB
)

if (-not $User) { $User = "postgres" }
if (-not $Db) { $Db = "crypto_exchange" }

$root = Split-Path -Parent $PSScriptRoot
$migrationsDir = Join-Path $root "backend\migrations"

Get-ChildItem -Path $migrationsDir -Filter "*.sql" | Sort-Object Name | ForEach-Object {
    Write-Host ">> $($_.Name)"
    Get-Content $_.FullName -Raw -Encoding UTF8 | docker compose exec -T postgres psql -U $User -d $Db
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
}
