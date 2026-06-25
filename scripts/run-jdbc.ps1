param(
    [int]$MinVersion = 21
)

function Get-JavaMajorVersion {
    param([string]$JavaExe)
    $output = & $JavaExe -version 2>&1 | Out-String
    if ($output -match 'version "(\d+)') {
        return [int]$Matches[1]
    }
    return 0
}

function Resolve-JdkHome {
    param([int]$MinVersion)

    if ($env:JAVA_HOME) {
        $javaExe = Join-Path $env:JAVA_HOME "bin\java.exe"
        if ((Test-Path $javaExe) -and ((Get-JavaMajorVersion $javaExe) -ge $MinVersion)) {
            return $env:JAVA_HOME
        }
    }

    $searchRoots = @(
        "C:\Program Files\Eclipse Adoptium",
        "C:\Program Files\Microsoft",
        "C:\Program Files\Java"
    )

    foreach ($root in $searchRoots) {
        if (-not (Test-Path $root)) { continue }

        $match = Get-ChildItem -Path $root -Directory -ErrorAction SilentlyContinue |
            Where-Object { $_.Name -match "^jdk-$MinVersion" -or $_.Name -match "^jdk-2[1-9]" } |
            Sort-Object Name -Descending |
            Select-Object -First 1

        if ($match) {
            return $match.FullName
        }
    }

    throw @"
JDK $MinVersion+ not found.

Install Temurin JDK 21 or set JAVA_HOME, for example:
  `$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.8.9-hotspot'
"@
}

$jdkHome = Resolve-JdkHome -MinVersion $MinVersion
$env:JAVA_HOME = $jdkHome
if ($env:Path -notlike "*$jdkHome\bin*") {
    $env:Path = "$jdkHome\bin;$env:Path"
}

$root = Split-Path -Parent $PSScriptRoot
$jdbcDir = Join-Path $root "backend\jdbc-console"

Push-Location $jdbcDir
try {
    & mvn -q compile exec:java
    exit $LASTEXITCODE
}
finally {
    Pop-Location
}
