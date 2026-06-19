# Patch v2.2 - Corrección de compilación Windows en GitHub Actions

## Problema corregido

GitHub Actions fallaba en el paso **Compilar instaladores Windows** con este error:

```text
java.nio.file.InvalidPathException: Illegal char <:> at index ...
D:\a\...\WindowsCalculadora-de-medicamentos-" :desktopApp:packageReleaseExe ...
```

La causa estaba en `gradlew.bat`.

En Windows, `%~dp0` devuelve la ruta del proyecto con una barra final `\`. Al pasar esa ruta entre comillas a Java, la barra final puede escapar la comilla de cierre y unir accidentalmente la ruta con los argumentos de Gradle.

Resultado: Java recibía una ruta inválida que incluía `" :desktopApp:packageReleaseExe`.

Windows haciendo Windows. Predecible, pero igual molesto.

## Archivo modificado

```text
gradlew.bat
```

## Cómo aplicar

Copia el archivo `gradlew.bat` de este parche y reemplaza el que está en la raíz del proyecto.

Debe quedar en:

```text
gradlew.bat
```

Luego súbelo a GitHub con commit, por ejemplo:

```text
fix: corregir gradlew.bat para GitHub Actions Windows
```

Después vuelve a correr el workflow:

```text
Actions → Windows - Compilar prueba → Run workflow
```

O si quieres publicar versión:

```text
Actions → Windows - Publicar actualización → Run workflow
```

## Resultado esperado

El comando:

```text
.\gradlew.bat :desktopApp:packageReleaseExe :desktopApp:packageReleaseMsi --build-cache --stacktrace
```

ya no debe convertir la ruta del proyecto en una ruta inválida.
