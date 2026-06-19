# Patch v2.3 - Corrección del actualizador Windows

## Error corregido

GitHub Actions fallaba en `:desktopApp:compileKotlin` por errores de tipos en:

```text
desktopApp/src/main/kotlin/com/luisangel/calculadoramedicamentos/updater/WindowsUpdateInstaller.kt
```

Errores vistos:

```text
Argument type mismatch: actual type is 'Path!', but 'File!' was expected.
Unresolved reference 'toAbsolutePath'.
```

## Cambio aplicado

Se corrigió el manejo de rutas del actualizador Windows:

- Se quitó `.toPath()` sobre `AppPaths.dataDir.resolve("updates")`.
- Se usa `Path` para crear/copiar el archivo descargado.
- Se convierte a `File` solo donde `ProcessBuilder.directory(...)` lo exige.
- Se reemplazó `destination.toAbsolutePath()` por `destinationFile.absolutePath`.

## Archivos modificados

```text
desktopApp/src/main/kotlin/com/luisangel/calculadoramedicamentos/updater/WindowsUpdateInstaller.kt
```

## Cómo aplicar

1. Descomprime este parche.
2. Copia la carpeta `desktopApp` encima de la raíz del proyecto.
3. Acepta reemplazar el archivo existente.
4. Sube el cambio a GitHub.
5. Ejecuta de nuevo el workflow de Windows.

Commit sugerido:

```text
fix: corregir rutas del actualizador Windows
```
