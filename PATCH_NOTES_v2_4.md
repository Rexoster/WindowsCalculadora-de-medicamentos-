# Patch v2.4 - Corrección ProGuard en Windows

## Problema corregido

GitHub Actions fallaba en:

```text
Task :desktopApp:proguardReleaseJars FAILED
External tool execution failed
```

El error aparece porque el workflow estaba usando las tareas `packageReleaseExe` y `packageReleaseMsi`. Esas tareas pasan por ProGuard (`proguardReleaseJars`) y en el runner de Windows fallaban antes de generar el instalador.

## Solución aplicada

Se cambiaron los workflows y scripts locales para usar las tareas sin ProGuard:

```text
:desktopApp:packageExe
:desktopApp:packageMsi
```

Estas tareas siguen generando instaladores de Windows, pero evitan el paso problemático de ProGuard.

## Archivos modificados

```text
.github/workflows/windows-build.yml
.github/workflows/windows-release.yml
github_workflows_para_subir_manualmente/windows-build.yml
github_workflows_para_subir_manualmente/windows-release.yml
build-windows-exe.bat
build-windows-msi.bat
```

## Cómo aplicar

1. Copia el contenido del parche encima de la raíz del proyecto.
2. Acepta reemplazar archivos.
3. Sube solo estos cambios a GitHub.
4. Ejecuta de nuevo:

```text
Actions → Windows - Compilar prueba → Run workflow
```

## Commit sugerido

```text
fix: evitar proguard en instaladores windows
```
