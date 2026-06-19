# Modo elegido: un solo repositorio público

Este proyecto queda configurado para trabajar con **un solo repositorio de GitHub**.

```text
CalculadoraMedicamentos
```

Ese mismo repositorio contiene:

```text
Código Android
Código Windows
Workflows de GitHub Actions
Releases con instaladores Windows
Releases con APK/AAB Android, si después los publicas
```

## Punto importante

Para que la app de Windows pueda actualizarse sola sin pedir usuario, contraseña o token, este repositorio debe ser:

```text
Public
```

Si lo haces privado, la app instalada no podrá consultar ni descargar los Releases de GitHub de forma anónima.

No se usará repositorio doble.
No se usará repositorio separado solo para Releases.
No se meterá token dentro de la app.

## Flujo final

```text
Subes cambios al mismo repositorio
↓
GitHub Actions compila Windows
↓
GitHub crea un Release en ese mismo repositorio
↓
La app Windows revisa los Releases de ese mismo repositorio
↓
Descarga el .msi o .exe
↓
Abre el instalador
```

## Workflows que se usan

```text
.github/workflows/windows-build.yml
.github/workflows/windows-release.yml
```

`windows-build.yml` compila pruebas cuando subes cambios.

`windows-release.yml` crea la actualización real.

## Cómo publicar una versión Windows

En GitHub:

```text
Actions
Windows - Publicar actualización
Run workflow
```

Usa una versión con tres números:

```text
3.1.1
```

El Release se creará en el mismo repositorio con nombre:

```text
windows-v3.1.1
```

## Qué debe descargar el usuario la primera vez

La primera instalación sí se hace manualmente desde GitHub:

```text
Releases → windows-v3.1.1 → descargar .msi o .exe
```

Después de instalada, las siguientes actualizaciones se pueden revisar desde la app.
