# Parche v2.1 - Workflows y regla de parches

Este parche NO reemplaza todo el proyecto.

Sirve para:

1. Agregar o restaurar la carpeta `.github/workflows`.
2. Dejar una copia alternativa de los workflows en `github_workflows_para_subir_manualmente/`.
3. Establecer que los cambios futuros se entreguen como parches mínimos.

## Archivos incluidos

```text
.github/workflows/build-apk.yml
.github/workflows/build-release.yml
.github/workflows/windows-build.yml
.github/workflows/windows-release.yml
github_workflows_para_subir_manualmente/*.yml
INSTALAR_WORKFLOWS_GITHUB.bat
APLICAR_PARCHE.bat
docs/PARCHES_DESDE_AHORA.md
PATCH_NOTES_v2_1.md
```

## Cómo aplicarlo

Extrae este parche encima de la raíz del proyecto.

La raíz del proyecto es donde están:

```text
app/
desktopApp/
build.gradle.kts
settings.gradle.kts
gradlew.bat
```

Después ejecuta:

```text
INSTALAR_WORKFLOWS_GITHUB.bat
```

Eso crea o rellena:

```text
.github/workflows/
```

## Si GitHub Web no sube `.github`

Crea los archivos manualmente en GitHub:

1. Entra al repositorio.
2. Click en `Add file`.
3. Click en `Create new file`.
4. En el nombre escribe exactamente:

```text
.github/workflows/windows-release.yml
```

5. Copia el contenido desde:

```text
github_workflows_para_subir_manualmente/windows-release.yml
```

Repite con los otros `.yml`.

## Regla desde ahora

Los cambios futuros deben entregarse como parche, no como proyecto completo.

Cada parche debe incluir solo:

- archivos nuevos,
- archivos modificados,
- instrucciones breves,
- y, si aplica, script para copiar o instalar el parche.

Nada de subir todo otra vez como si GitHub fuera mudanza de casa.
