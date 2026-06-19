# Actualizaciones Windows con GitHub Releases

## Modo configurado

Se usa **un solo repositorio público**. El código, GitHub Actions y los Releases viven en el mismo repositorio.

La app Windows usa este flujo:

```text
AppBuildInfo.versionName
↓
GitHub Releases latest
↓
update-windows.json o instalador .msi/.exe
↓
Comparación de versiones
↓
Descarga y apertura del instalador
```

## Archivo generado por GitHub Actions

El workflow `Windows - Publicar actualización` genera:

```json
{
  "versionName": "3.1.1",
  "versionCode": 123,
  "platform": "windows",
  "fileName": "CalculadoraMedicamentos-3.1.1.msi",
  "downloadUrl": "https://github.com/USUARIO/REPO/releases/download/windows-v3.1.1/CalculadoraMedicamentos-3.1.1.msi",
  "releaseUrl": "https://github.com/USUARIO/REPO/releases/tag/windows-v3.1.1",
  "notes": "Actualización de Calculadora de Medicamentos para Windows."
}
```

## Dónde se descarga el instalador

La app guarda instaladores descargados en:

```text
%APPDATA%\CalculadoraMedicamentos\updates\
```

## Cómo compara versiones

La app compara versiones numéricas:

```text
3.1.1 < 3.1.2
3.1.2 < 3.2.0
```

Usa siempre formato:

```text
X.Y.Z
```

Ejemplo:

```text
3.1.2
```

No uses:

```text
3.1.2-beta
junio-final-ahora-sí
version-buena-no-tocar
```

Windows y GitHub ya tienen suficientes dramas.
