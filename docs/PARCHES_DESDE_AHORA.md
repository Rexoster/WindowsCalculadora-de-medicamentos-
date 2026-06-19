# Parches desde ahora

A partir de esta versión, las actualizaciones del proyecto deben manejarse como parches mínimos.

## Qué significa

No se debe reemplazar todo el proyecto cada vez.

Cada cambio nuevo debe entregarse como un ZIP pequeño con solo:

```text
archivos modificados
archivos nuevos
instrucciones del parche
scripts necesarios
```

## Cómo aplicar un parche

1. Haz copia de seguridad o commit antes de aplicar.
2. Extrae el ZIP del parche encima de la raíz del proyecto.
3. Acepta reemplazar archivos si Windows lo pregunta.
4. Ejecuta el script del parche si viene incluido.
5. Revisa cambios.
6. Sube a GitHub.

## Con Git

```bash
git status
git add -A
git commit -m "Aplicar parche"
git push
```

## Sobre `.github`

GitHub Actions solo detecta workflows si están dentro de:

```text
.github/workflows/
```

Si la carpeta `.github` no se sube desde Windows o desde el navegador, se puede crear manualmente desde GitHub con `Create new file` usando una ruta como:

```text
.github/workflows/windows-release.yml
```

También se puede usar GitHub Desktop o Git normal. Eso sí sube carpetas con punto sin ponerse dramático.
