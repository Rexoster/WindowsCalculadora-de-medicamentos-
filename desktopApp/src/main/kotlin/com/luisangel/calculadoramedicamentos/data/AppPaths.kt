package com.luisangel.calculadoramedicamentos.data

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories

object AppPaths {
    val dataDir: Path by lazy {
        val appData = System.getenv("APPDATA")?.takeIf { it.isNotBlank() }
        val base = if (appData != null) {
            Path.of(appData, "CalculadoraMedicamentos")
        } else {
            Path.of(System.getProperty("user.home"), ".calculadora-medicamentos")
        }
        base.createDirectories()
    }

    val medicationsFile: Path get() = dataDir.resolve("medications.json")
    val preferencesFile: Path get() = dataDir.resolve("preferences.properties")
    val downloadsDir: Path get() = dataDir.resolve("downloads").also { Files.createDirectories(it) }
}
