# Calculadora de Medicamentos

Aplicación médica en dos versiones dentro del mismo proyecto:

- **Android nativo**: módulo `app`.
- **Windows / escritorio**: módulo `desktopApp`, hecho con Kotlin + Compose Desktop.

La versión Windows puede compilarse automáticamente con **GitHub Actions** y publicarse como instalador `.exe` y `.msi` en **GitHub Releases**. La app de Windows puede buscar actualizaciones desde su sección **Actualizaciones** y descargar el instalador publicado.

## Primer uso en GitHub

Lee primero:

```text
SUBIR_A_GITHUB_DESDE_CERO.md
MODO_REPOSITORIO_UNICO.md
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

Importante: el proyecto queda en **un solo repositorio público**. Ese mismo repositorio contiene el código y los Releases. Si lo haces privado, la app Windows no podrá actualizarse sola sin autenticación. No se usa repositorio doble.

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
