# Calculadora de Medicamentos

Aplicación médica en dos versiones dentro del mismo proyecto:

- **Android nativo**: módulo `app`.
- **Windows / escritorio**: módulo `desktopApp`, hecho con Kotlin + Compose Desktop.

La versión Windows puede compilarse automáticamente con **GitHub Actions** y publicarse como instalador `.exe` y `.msi` en **GitHub Releases**. La app de Windows puede buscar actualizaciones desde su sección **Actualizaciones** y descargar el instalador publicado.

## Primer uso en GitHub

Lee primero:

```text
SUBIR_A_GITHUB_DESDE_CERO.md
```

Sí, hay un manual. La civilización todavía depende de leer cuatro pasos antes de presionar botones.

## Ejecutar Windows localmente

En Windows:

```bat
run-windows.bat
```

O manual:

```bat
gradlew.bat :desktopApp:run
```

## Generar instalador Windows localmente

```bat
build-windows-exe.bat
build-windows-msi.bat
```

Los instaladores quedan en:

```text
desktopApp\build\compose\binaries\main-release\
```

## Actualizaciones Windows

El flujo preparado es:

```text
GitHub Actions compila → crea Release → sube .msi/.exe + update-windows.json → la app consulta GitHub → descarga e instala
```

Importante: para que la app pueda consultar actualizaciones sin token, el repositorio donde estén los Releases debe ser **público**. Si el repo es privado, GitHub no entrega Releases a una app anónima. Meter un token dentro de la app sería esconder la llave bajo el tapete y luego sorprenderse porque alguien abrió la puerta.

## Workflows incluidos

```text
.github/workflows/windows-build.yml
.github/workflows/windows-release.yml
.github/workflows/build-apk.yml
.github/workflows/build-release.yml
```

- `windows-build.yml`: compila instaladores de prueba al subir cambios a `main`.
- `windows-release.yml`: crea una actualización publicable desde GitHub Actions.
- `build-apk.yml`: conserva la compilación Android debug.
- `build-release.yml`: conserva la publicación Android firmada si configuras los secretos de firma.
