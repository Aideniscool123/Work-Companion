$ErrorActionPreference = 'Stop'
$root = $PSScriptRoot
$jdk = $env:JAVA_HOME
if (!$jdk -or !(Test-Path (Join-Path $jdk 'bin\jpackage.exe'))) {
    $jdk = Get-ChildItem (Join-Path $env:ProgramFiles 'Java') -Directory -Filter 'jdk*' -ErrorAction SilentlyContinue |
        Where-Object { Test-Path (Join-Path $_.FullName 'bin\jpackage.exe') } |
        Sort-Object Name -Descending | Select-Object -First 1 -ExpandProperty FullName
}
if (!$jdk) { throw 'A full JDK 17 or newer with jpackage is required.' }
$javac = Join-Path $jdk 'bin\javac.exe'
$jpackage = Join-Path $jdk 'bin\jpackage.exe'
$classes = Join-Path $root 'build\classes'
New-Item -ItemType Directory -Force -Path $classes | Out-Null
& $javac -encoding UTF-8 -d $classes (Join-Path $root 'src\main\java\com\workcompanion\WorkCompanion.java')
if ($LASTEXITCODE -ne 0) { throw 'Java compilation failed.' }
$jar = Join-Path $root 'build\WorkCompanion.jar'
& (Join-Path $jdk 'bin\jar.exe') --create --file $jar --main-class com.workcompanion.WorkCompanion -C $classes .
if ($LASTEXITCODE -ne 0) { throw 'JAR creation failed.' }
$dist = Join-Path $root 'dist'
if (Test-Path $dist) { Remove-Item -LiteralPath $dist -Recurse -Force }
& $jpackage --type app-image --name WorkCompanion --input (Split-Path $jar) --main-jar (Split-Path $jar -Leaf) --main-class com.workcompanion.WorkCompanion --dest $dist --vendor 'Work Companion' --app-version 0.1.0
if ($LASTEXITCODE -ne 0) { throw 'Windows app packaging failed.' }
Write-Host "Built: $(Join-Path $dist 'WorkCompanion\WorkCompanion.exe')"
