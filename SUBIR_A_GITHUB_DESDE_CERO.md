# Subir este proyecto a GitHub desde cero

Este ZIP está pensado para crear un repositorio nuevo y que GitHub compile la app de Windows automáticamente.

## 1. Crear el repositorio

En GitHub crea un repositorio nuevo, por ejemplo:

```text
CalculadoraMedicamentos
```

Recomendado para actualizaciones automáticas Windows:

```text
Repositorio público
```

Motivo: la app instalada consulta `https://api.github.com/repos/USUARIO/REPO/releases/latest`. Si el repositorio es privado, GitHub exige autenticación. La app no debe traer un token adentro, porque eso es regalar credenciales con moño.

## 2. Subir los archivos

No subas este ZIP como un único archivo dentro del repo.

Haz esto:

1. Descomprime el ZIP.
2. Entra a la carpeta descomprimida.
3. Sube **todo el contenido** al repositorio, incluyendo:

```text
.github/
app/
desktopApp/
gradle/
build.gradle.kts
settings.gradle.kts
gradlew
gradlew.bat
```

Si usas Git en terminal:

```bash
git init
git add .
git commit -m "Primera versión Android y Windows"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/TU_REPOSITORIO.git
git push -u origin main
```

## 3. Activar permisos de GitHub Actions

En tu repositorio:

```text
Settings → Actions → General → Workflow permissions
```

Selecciona:

```text
Read and write permissions
```

Esto permite que el workflow cree Releases. Sí, GitHub necesita permiso para publicar en GitHub. Un pequeño círculo burocrático perfecto.

## 4. Compilar prueba Windows

Al subir a `main`, debe correr automáticamente:

```text
Windows - Compilar prueba
```

También puedes correrlo manualmente:

```text
Actions → Windows - Compilar prueba → Run workflow
```

Ese workflow genera instaladores como artefactos, pero todavía no publica actualización oficial.

## 5. Crear la primera actualización Windows

En GitHub:

```text
Actions → Windows - Publicar actualización → Run workflow
```

Usa una versión de tres números, por ejemplo:

```text
3.1.1
```

Ese workflow hace esto:

```text
Compila .exe y .msi
Crea dist/update-windows.json
Crea un Release llamado windows-v3.1.1
Sube los instaladores
```

## 6. Instalar en una computadora Windows

Después de que el Release se cree:

```text
Releases → windows-v3.1.1 → descarga el .msi o .exe
```

Instala la app.

## 7. Probar actualización desde la app

Para probar que se actualiza sola:

1. Cambia algo en el código.
2. Ejecuta el workflow `Windows - Publicar actualización` con una versión mayor, por ejemplo:

```text
3.1.2
```

3. Abre la app instalada.
4. Ve a:

```text
Actualizaciones → Buscar actualizaciones
```

La app debe detectar la versión nueva, descargar el instalador y abrirlo.

## 8. Android sigue existiendo

El módulo Android sigue en:

```text
app/
```

Y conserva sus workflows:

```text
.github/workflows/build-apk.yml
.github/workflows/build-release.yml
```

Para Android firmado necesitarás configurar los secretos de firma que ya manejaba el proyecto.

## 9. Archivos importantes del sistema Windows

```text
desktopApp/src/main/kotlin/com/luisangel/calculadoramedicamentos/updater/AppBuildInfo.kt
desktopApp/src/main/kotlin/com/luisangel/calculadoramedicamentos/updater/UpdateChecker.kt
desktopApp/src/main/kotlin/com/luisangel/calculadoramedicamentos/updater/WindowsUpdateInstaller.kt
.github/workflows/windows-release.yml
```

## 10. Qué NO hacer

No metas un token de GitHub dentro de la app para leer un repo privado.

Eso funciona técnicamente, igual que dejar una receta médica pegada en la puerta del consultorio también “funciona” para que todos la vean. No significa que sea buena idea.
