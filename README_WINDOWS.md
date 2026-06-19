# Calculadora de Medicamentos · Windows

Conversión de la app Android nativa a aplicación de escritorio para Windows usando **Kotlin + Compose Desktop**.

## Qué se conserva

- Módulo Android original intacto en `app/`.
- Nuevo módulo Windows/Desktop en `desktopApp/`.
- Catálogo local de medicamentos.
- Adultos, pediátricos y adultos especiales por kg.
- Dosis con unidad visible en la misma celda de dosis.
- Filtros por texto, familia, subgrupo, tipo y especialidades.
- Familia, subgrupo y especialidades se usan para filtrar, pero no aparecen como columnas en la tabla principal.
- Agregar, editar, eliminar y borrar medicamentos.
- Importación y exportación XLSX/XLS/CSV/JSON con detección de duplicados.
- Percentiles pediátricos con tablas LMS locales.
- Calculadoras obstétricas nativas.
- Calculadora renal nativa.
- Tema claro/oscuro.

## Qué cambia por ser Windows

- Se reemplaza Room/DataStore por almacenamiento local JSON/Properties.
- Los datos quedan en:
  - Windows: `%APPDATA%\CalculadoraMedicamentos\`
  - Otros sistemas: `~/.calculadora-medicamentos/`
- La actualización tipo APK no aplica en Windows. Para Windows se genera un instalador `.exe` o `.msi` nuevo.
- No se incluyó la integración de medicamentos desde el PDF de enfermedades infecciosas, porque para esta versión quedó fuera.

## Cómo ejecutar en desarrollo

Desde la carpeta del proyecto:

```bat
gradlew.bat :desktopApp:run
```

## Cómo crear instalador de Windows

Para crear `.exe`:

```bat
gradlew.bat :desktopApp:packageReleaseExe
```

Para crear `.msi`:

```bat
gradlew.bat :desktopApp:packageReleaseMsi
```

Salida esperada:

```text
desktopApp\build\compose\binaries\main-release\exe\
desktopApp\build\compose\binaries\main-release\msi\
```

## Requisitos

- Windows 10/11 de 64 bits.
- JDK 17 o superior para compilar.
- Internet la primera vez para descargar Gradle y dependencias.

## Archivos clave agregados

```text
desktopApp/build.gradle.kts
desktopApp/src/main/kotlin/com/luisangel/calculadoramedicamentos/Main.kt
desktopApp/src/main/kotlin/com/luisangel/calculadoramedicamentos/ui/DesktopApp.kt
desktopApp/src/main/kotlin/com/luisangel/calculadoramedicamentos/ui/DesktopMainViewModel.kt
desktopApp/src/main/kotlin/com/luisangel/calculadoramedicamentos/data/AppPaths.kt
desktopApp/src/main/kotlin/com/luisangel/calculadoramedicamentos/data/AppPreferences.kt
desktopApp/src/main/kotlin/com/luisangel/calculadoramedicamentos/data/MedicationRepository.kt
```

## Actualización automática desde la app

Esta versión incluye actualizador para Windows:

```text
Actualizaciones → Buscar actualizaciones → Descargar e instalar
```

La app consulta los Releases del repositorio configurado por GitHub Actions. El workflow responsable es:

```text
.github/workflows/windows-release.yml
```

Al publicar una versión, el workflow genera:

```text
.msi
.exe
update-windows.json
```

La app prefiere `.msi` si existe; si no, usa `.exe`.

Para que funcione sin login, el repositorio de Releases debe ser público.
