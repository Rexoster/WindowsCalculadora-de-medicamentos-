package com.luisangel.calculadoramedicamentos

import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.luisangel.calculadoramedicamentos.data.AppPreferences
import com.luisangel.calculadoramedicamentos.data.MedicationRepository
import com.luisangel.calculadoramedicamentos.io.ExcelService
import com.luisangel.calculadoramedicamentos.ui.DesktopCalculatorApp
import com.luisangel.calculadoramedicamentos.ui.DesktopMainViewModel

fun main() = application {
    val viewModel = DesktopMainViewModel(
        repository = MedicationRepository(),
        preferences = AppPreferences(),
        excelServiceProvider = ::ExcelService
    )

    DisposableEffect(Unit) {
        onDispose { viewModel.close() }
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Calculadora de Medicamentos · Windows",
        state = rememberWindowState(width = 1280.dp, height = 820.dp)
    ) {
        DesktopCalculatorApp(viewModel)
    }
}
