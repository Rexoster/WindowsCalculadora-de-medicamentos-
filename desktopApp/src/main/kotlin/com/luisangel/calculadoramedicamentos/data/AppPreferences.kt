package com.luisangel.calculadoramedicamentos.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Properties
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

class AppPreferences(
    private val file: java.nio.file.Path = AppPaths.preferencesFile
) {
    private val properties = Properties()
    private val _darkTheme = MutableStateFlow(false)
    private val _examplesSeeded = MutableStateFlow(false)

    val darkTheme: StateFlow<Boolean> = _darkTheme
    val examplesSeeded: StateFlow<Boolean> = _examplesSeeded

    init {
        load()
    }

    @Synchronized
    private fun load() {
        if (file.exists()) {
            file.inputStream().use(properties::load)
        }
        _darkTheme.value = properties.getProperty("dark_theme", "false").toBooleanStrictOrNull() ?: false
        _examplesSeeded.value = properties.getProperty("examples_seeded", "false").toBooleanStrictOrNull() ?: false
    }

    @Synchronized
    private fun save() {
        properties.setProperty("dark_theme", _darkTheme.value.toString())
        properties.setProperty("examples_seeded", _examplesSeeded.value.toString())
        file.outputStream().use { properties.store(it, "Calculadora de Medicamentos · Windows") }
    }

    suspend fun setDarkTheme(value: Boolean) {
        _darkTheme.value = value
        save()
    }

    suspend fun setExamplesSeeded(value: Boolean) {
        _examplesSeeded.value = value
        save()
    }
}
