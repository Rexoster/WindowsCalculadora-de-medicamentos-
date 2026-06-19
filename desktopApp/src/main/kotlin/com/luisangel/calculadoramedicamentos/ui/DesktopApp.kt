@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.luisangel.calculadoramedicamentos.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.PregnantWoman
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.luisangel.calculadoramedicamentos.data.AppPaths
import com.luisangel.calculadoramedicamentos.growth.GrowthAssessment
import com.luisangel.calculadoramedicamentos.growth.GrowthChart
import com.luisangel.calculadoramedicamentos.growth.GrowthEngine
import com.luisangel.calculadoramedicamentos.growth.GrowthResult
import com.luisangel.calculadoramedicamentos.growth.GrowthSex
import com.luisangel.calculadoramedicamentos.growth.MeasurementMode
import com.luisangel.calculadoramedicamentos.model.FilterState
import com.luisangel.calculadoramedicamentos.model.MedicationDraft
import com.luisangel.calculadoramedicamentos.model.MedicationRecord
import com.luisangel.calculadoramedicamentos.model.MedicationType
import com.luisangel.calculadoramedicamentos.model.SortOption
import com.luisangel.calculadoramedicamentos.model.TypeFilter
import com.luisangel.calculadoramedicamentos.model.calculatedDose
import com.luisangel.calculadoramedicamentos.model.toDraft
import com.luisangel.calculadoramedicamentos.obstetrics.UltrasoundDatingCalculator
import com.luisangel.calculadoramedicamentos.obstetrics.UltrasoundDatingResult
import com.luisangel.calculadoramedicamentos.obstetrics.UltrasoundTrimester
import com.luisangel.calculadoramedicamentos.renal.AcrUnit
import com.luisangel.calculadoramedicamentos.renal.CreatinineUnit
import com.luisangel.calculadoramedicamentos.renal.RenalCalculator
import com.luisangel.calculadoramedicamentos.renal.RenalInput
import com.luisangel.calculadoramedicamentos.renal.RenalMethod
import com.luisangel.calculadoramedicamentos.renal.RenalResult
import com.luisangel.calculadoramedicamentos.renal.RenalSex
import com.luisangel.calculadoramedicamentos.updater.AppBuildInfo
import com.luisangel.calculadoramedicamentos.updater.UpdateCheckResult
import com.luisangel.calculadoramedicamentos.updater.UpdateChecker
import com.luisangel.calculadoramedicamentos.updater.WindowsUpdateInstaller
import kotlinx.coroutines.launch
import java.io.File
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.math.max
import kotlin.math.min

private enum class DesktopSection(val label: String, val icon: ImageVector) {
    MEDS("Medicamentos", Icons.Default.Medication),
    GROWTH("Percentiles", Icons.Default.MonitorHeart),
    OBSTETRICS("Obstetricia", Icons.Default.PregnantWoman),
    RENAL("Renal", Icons.Default.Calculate),
    UPDATES("Actualizaciones", Icons.Default.Download)
}

private val df = DecimalFormat("0.##")
private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

@Composable
fun DesktopCalculatorApp(viewModel: DesktopMainViewModel) {
    val darkTheme by viewModel.darkTheme.collectAsState()
    var section by remember { mutableStateOf(DesktopSection.MEDS) }
    var statusMessage by remember { mutableStateOf("Lista para trabajar. Milagro administrativo en formato escritorio.") }

    LaunchedEffect(viewModel) {
        viewModel.messages.collect { statusMessage = it }
    }

    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()
    ) {
        Surface(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxSize()) {
                NavigationRail(
                    selected = section,
                    onSelected = { section = it },
                    darkTheme = darkTheme,
                    onToggleTheme = viewModel::toggleTheme
                )
                Divider(Modifier.fillMaxHeight().width(1.dp))
                Column(Modifier.fillMaxSize()) {
                    Header(statusMessage = statusMessage)
                    Box(Modifier.fillMaxSize().padding(16.dp)) {
                        when (section) {
                            DesktopSection.MEDS -> MedicationScreen(viewModel)
                            DesktopSection.GROWTH -> GrowthScreen()
                            DesktopSection.OBSTETRICS -> ObstetricsScreen()
                            DesktopSection.RENAL -> RenalScreen()
                            DesktopSection.UPDATES -> UpdatesScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigationRail(
    selected: DesktopSection,
    onSelected: (DesktopSection) -> Unit,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    Column(
        modifier = Modifier.width(230.dp).fillMaxHeight().padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Calculadora", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        Text("Medicamentos · Windows", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Divider()
        DesktopSection.entries.forEach { item ->
            val active = item == selected
            Surface(
                color = if (active) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().clickable { onSelected(item) }
            ) {
                Row(
                    Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(item.icon, contentDescription = null)
                    Text(item.label, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
        Spacer(Modifier.weight(1f))
        OutlinedButton(onClick = onToggleTheme, modifier = Modifier.fillMaxWidth()) {
            Text(if (darkTheme) "Tema claro" else "Tema oscuro")
        }
    }
}

@Composable
private fun Header(statusMessage: String) {
    Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Calculadora de Medicamentos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Text("Aplicación local de Windows. Sin WebView, porque todavía respetamos un poco la dignidad humana.", style = MaterialTheme.typography.bodySmall)
            }
            Text(statusMessage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun MedicationScreen(viewModel: DesktopMainViewModel) {
    val all by viewModel.allMedications.collectAsState()
    val filtered by viewModel.filteredMedications.collectAsState()
    val filters by viewModel.filters.collectAsState()
    val adultWeight by viewModel.adultWeight.collectAsState()
    val pediatricWeight by viewModel.pediatricWeight.collectAsState()
    val families by viewModel.availableFamilies.collectAsState()
    val subgroups by viewModel.availableSubgroups.collectAsState()
    val specialties by viewModel.availableSpecialties.collectAsState()
    val pendingImport by viewModel.pendingImport.collectAsState()
    val busy by viewModel.busy.collectAsState()

    var editing by remember { mutableStateOf<MedicationRecord?>(null) }
    var selected by remember { mutableStateOf<MedicationRecord?>(null) }
    var confirmClear by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(true) }

    if (editing != null) {
        MedicationEditorDialog(
            record = editing,
            families = families,
            subgroups = subgroups,
            specialties = specialties,
            onDismiss = { editing = null },
            onSave = { draft, original ->
                viewModel.saveMedication(draft, original)
                editing = null
            }
        )
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("Borrar todo") },
            text = { Text("Esto elimina todos los medicamentos guardados en esta instalación de Windows. Sí, todos. La guillotina de datos.") },
            confirmButton = { TextButton(onClick = { viewModel.clearAll(); confirmClear = false; selected = null }) { Text("Borrar") } },
            dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("Cancelar") } }
        )
    }

    pendingImport?.let { preview ->
        AlertDialog(
            onDismissRequest = viewModel::cancelImport,
            title = { Text("Vista previa de importación") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Registros únicos: ${preview.uniqueRecords.size}")
                    Text("Nuevos: ${preview.newRecords.size}")
                    Text("Repetidos dentro del archivo: ${preview.duplicatesInsideFile}")
                    Text("Ya existentes en tu base: ${preview.duplicatesWithCurrent}")
                }
            },
            confirmButton = { Button(onClick = { viewModel.finishImport(replace = false) }) { Text("Agregar nuevos") } },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { viewModel.finishImport(replace = true) }) { Text("Reemplazar todo") }
                    TextButton(onClick = viewModel::cancelImport) { Text("Cancelar") }
                }
            }
        )
    }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { editing = MedicationRecord(id = "") }) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(6.dp)); Text("Agregar") }
            OutlinedButton(enabled = selected != null, onClick = { selected?.let { editing = it } }) { Icon(Icons.Default.Edit, null); Spacer(Modifier.width(6.dp)); Text("Editar") }
            OutlinedButton(enabled = selected != null, onClick = { selected?.let { viewModel.deleteMedication(it); selected = null } }) { Icon(Icons.Default.Delete, null); Spacer(Modifier.width(6.dp)); Text("Eliminar") }
            OutlinedButton(onClick = { chooseImportFile()?.let(viewModel::importFromFile) }) { Icon(Icons.Default.UploadFile, null); Spacer(Modifier.width(6.dp)); Text("Importar") }
            OutlinedButton(onClick = { chooseExportFile()?.let(viewModel::exportToFile) }) { Icon(Icons.Default.Save, null); Spacer(Modifier.width(6.dp)); Text("Exportar") }
            OutlinedButton(onClick = { confirmClear = true }) { Text("Borrar todo") }
            if (busy) CircularProgressIndicator(Modifier.size(24.dp))
            Spacer(Modifier.weight(1f))
            AssistChip(onClick = { showFilters = !showFilters }, label = { Text(if (showFilters) "Ocultar filtros" else "Mostrar filtros") }, leadingIcon = { Icon(Icons.Default.FilterAlt, null) })
            AssistChip(onClick = {}, label = { Text("${filtered.size}/${all.size} visibles") })
        }

        if (showFilters) {
            FilterPanel(
                filters = filters,
                families = families,
                subgroups = subgroups,
                specialties = specialties,
                adultWeight = adultWeight,
                pediatricWeight = pediatricWeight,
                onFilters = viewModel::setFilters,
                onClear = viewModel::clearFilters,
                onAdultWeight = viewModel::setAdultWeight,
                onPediatricWeight = viewModel::setPediatricWeight
            )
        }

        MedicationTable(
            records = filtered,
            selected = selected,
            adultWeight = adultWeight.toDoubleOrNullMx(),
            pediatricWeight = pediatricWeight.toDoubleOrNullMx(),
            onSelected = { selected = it },
            onEdit = { editing = it }
        )
    }
}

@Composable
private fun FilterPanel(
    filters: FilterState,
    families: List<String>,
    subgroups: List<String>,
    specialties: List<String>,
    adultWeight: String,
    pediatricWeight: String,
    onFilters: (FilterState) -> Unit,
    onClear: () -> Unit,
    onAdultWeight: (String) -> Unit,
    onPediatricWeight: (String) -> Unit
) {
    OutlinedCard(colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = filters.search,
                    onValueChange = { onFilters(filters.copy(search = it)) },
                    label = { Text("Buscar") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.weight(1.2f)
                )
                StringMenu("Familia", filters.family, listOf("") + families, { it.ifBlank { "Todas" } }) { onFilters(filters.copy(family = it)) }
                StringMenu("Subgrupo", filters.subgroup, listOf("") + subgroups, { it.ifBlank { "Todos" } }) { onFilters(filters.copy(subgroup = it)) }
                EnumMenu("Tipo", filters.type, TypeFilter.entries, { typeFilterLabel(it) }) { onFilters(filters.copy(type = it)) }
                EnumMenu("Orden", filters.sort, SortOption.entries, { sortLabel(it) }) { onFilters(filters.copy(sort = it)) }
                OutlinedButton(onClick = { onFilters(filters.copy(ascending = !filters.ascending)) }) { Text(if (filters.ascending) "ASC" else "DESC") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(adultWeight, onAdultWeight, label = { Text("Peso adulto kg") }, modifier = Modifier.width(170.dp))
                OutlinedTextField(pediatricWeight, onPediatricWeight, label = { Text("Peso pediátrico kg") }, modifier = Modifier.width(190.dp))
                SpecialtyFilterMenu(specialties = specialties, selected = filters.specialties) { onFilters(filters.copy(specialties = it)) }
                OutlinedButton(onClick = onClear) { Text("Limpiar filtros") }
            }
        }
    }
}

@Composable
private fun MedicationTable(
    records: List<MedicationRecord>,
    selected: MedicationRecord?,
    adultWeight: Double?,
    pediatricWeight: Double?,
    onSelected: (MedicationRecord) -> Unit,
    onEdit: (MedicationRecord) -> Unit
) {
    val horizontal = rememberScrollState()
    OutlinedCard(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().horizontalScroll(horizontal)) {
            LazyColumn(Modifier.width(1540.dp).fillMaxHeight()) {
                item {
                    Row(Modifier.background(MaterialTheme.colorScheme.primaryContainer).padding(vertical = 8.dp)) {
                        HeaderCell("Medicamento", 180)
                        HeaderCell("Presentación", 230)
                        HeaderCell("Tipo", 120)
                        HeaderCell("Dosis", 230)
                        HeaderCell("Cálculo", 180)
                        HeaderCell("Frecuencia", 220)
                        HeaderCell("Duración", 110)
                        HeaderCell("Notas", 270)
                    }
                }
                if (records.isEmpty()) {
                    item { Text("No hay medicamentos con esos filtros. La tabla está más sola que Excel a las 3 a.m.", Modifier.padding(18.dp)) }
                } else {
                    items(records, key = { it.id }) { record ->
                        val isSelected = selected?.id == record.id
                        val weight = if (record.type == MedicationType.PEDIATRIC) pediatricWeight else adultWeight
                        Row(
                            Modifier
                                .background(if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
                                .combinedClickable(onClick = { onSelected(record) }, onDoubleClick = { onEdit(record) })
                                .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            BodyCell(record.name, 180, bold = true)
                            BodyCell(record.presentation, 230)
                            BodyCell(recordTypeLabel(record), 120)
                            BodyCell(doseWithUnit(record), 230)
                            BodyCell(record.calculatedDose(weight), 180)
                            BodyCell(record.frequencyPerDay, 220)
                            BodyCell("${record.durationDays} día(s)", 110)
                            BodyCell(record.notes, 270)
                        }
                    }
                }
            }
        }
    }
}

@Composable private fun HeaderCell(text: String, width: Int) { Text(text, Modifier.width(width.dp).padding(horizontal = 8.dp), fontWeight = FontWeight.Bold) }
@Composable private fun BodyCell(text: String, width: Int, bold: Boolean = false) { Text(text, Modifier.width(width.dp).padding(horizontal = 8.dp), maxLines = 4, overflow = TextOverflow.Ellipsis, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal) }

@Composable
private fun MedicationEditorDialog(
    record: MedicationRecord?,
    families: List<String>,
    subgroups: List<String>,
    specialties: List<String>,
    onDismiss: () -> Unit,
    onSave: (MedicationDraft, MedicationRecord?) -> Unit
) {
    var draft by remember(record) { mutableStateOf(record?.toDraft() ?: MedicationDraft()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (record?.id.isNullOrBlank()) "Agregar medicamento" else "Editar medicamento") },
        text = {
            Column(Modifier.width(760.dp).heightIn(max = 620.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    EnumMenu("Tipo", draft.type, MedicationType.entries, { if (it == MedicationType.ADULT) "Adulto" else "Pediátrico" }) { draft = draft.copy(type = it) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = draft.isSpecialAdult, enabled = draft.type == MedicationType.ADULT, onCheckedChange = { draft = draft.copy(isSpecialAdult = it) })
                        Text("Adulto especial por kg")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = draft.isInteractiveDose, enabled = draft.type == MedicationType.PEDIATRIC || draft.isSpecialAdult, onCheckedChange = { draft = draft.copy(isInteractiveDose = it) })
                        Text("Dosis interactiva")
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(draft.name, { draft = draft.copy(name = it) }, label = { Text("Medicamento") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(draft.presentation, { draft = draft.copy(presentation = it) }, label = { Text("Presentación") }, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(draft.dose, { draft = draft.copy(dose = it) }, label = { Text("Dosis fija o descripción") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(draft.doseUnit, { draft = draft.copy(doseUnit = it) }, label = { Text("Unidad") }, modifier = Modifier.width(120.dp))
                    OutlinedTextField(draft.frequencyPerDay, { draft = draft.copy(frequencyPerDay = it) }, label = { Text("Frecuencia") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(draft.durationDays, { draft = draft.copy(durationDays = it.filter(Char::isDigit)) }, label = { Text("Días") }, modifier = Modifier.width(100.dp))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(draft.dosePerKg, { draft = draft.copy(dosePerKg = it.numericText()) }, label = { Text("Dosis/kg inicial") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(draft.dosePerKgMin, { draft = draft.copy(dosePerKgMin = it.numericText()) }, label = { Text("Mín/kg") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(draft.dosePerKgMax, { draft = draft.copy(dosePerKgMax = it.numericText()) }, label = { Text("Máx/kg") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(draft.dosePerKgStep, { draft = draft.copy(dosePerKgStep = it.numericText()) }, label = { Text("Paso") }, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    EditableSuggestionField("Familia", draft.family, families, Modifier.weight(1f)) { draft = draft.copy(family = it) }
                    EditableSuggestionField("Subgrupo", draft.subgroup, subgroups, Modifier.weight(1f)) { draft = draft.copy(subgroup = it) }
                }
                Text("Especialidades", fontWeight = FontWeight.Bold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    specialties.forEach { item ->
                        FilterChip(
                            selected = item in draft.specialties,
                            onClick = { draft = draft.copy(specialties = draft.specialties.toggle(item)) },
                            label = { Text(item, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        )
                    }
                }
                OutlinedTextField(draft.notes, { draft = draft.copy(notes = it) }, label = { Text("Notas") }, minLines = 3, modifier = Modifier.fillMaxWidth())
                Text("La familia, subgrupo y especialidad se guardan para filtros, pero no se muestran en la tabla principal. Por fin una tabla que no parece inventario de ferretería.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        confirmButton = { Button(onClick = { onSave(draft, record?.takeIf { it.id.isNotBlank() }) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun GrowthScreen() {
    val engine = remember { GrowthEngine() }
    var sex by remember { mutableStateOf(GrowthSex.FEMALE) }
    var mode by remember { mutableStateOf(MeasurementMode.HEIGHT) }
    var birthDate by remember { mutableStateOf(LocalDate.now().minusYears(5).format(dateFormatter)) }
    var measurementDate by remember { mutableStateOf(LocalDate.now().format(dateFormatter)) }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var assessment by remember { mutableStateOf<GrowthAssessment?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedChart by remember { mutableStateOf<GrowthResult?>(null) }

    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedCard(Modifier.width(360.dp).fillMaxHeight()) {
            Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Percentiles pediátricos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                EnumMenu("Sexo", sex, GrowthSex.entries, { it.label }) { sex = it }
                EnumMenu("Medición", mode, MeasurementMode.entries, { it.label }) { mode = it }
                OutlinedTextField(birthDate, { birthDate = it }, label = { Text("Nacimiento AAAA-MM-DD") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(measurementDate, { measurementDate = it }, label = { Text("Medición AAAA-MM-DD") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(weight, { weight = it.numericText() }, label = { Text("Peso kg") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(height, { height = it.numericText() }, label = { Text("Talla/longitud cm") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = {
                    val result = runCatching {
                        engine.assess(
                            sex = sex,
                            birthDate = LocalDate.parse(birthDate, dateFormatter),
                            measurementDate = LocalDate.parse(measurementDate, dateFormatter),
                            weightKg = requireNotNull(weight.toDoubleOrNullMx()) { "Captura peso." },
                            heightCm = requireNotNull(height.toDoubleOrNullMx()) { "Captura talla." },
                            measurementMode = mode
                        ).getOrThrow()
                    }
                    assessment = result.getOrNull()
                    selectedChart = assessment?.results?.firstOrNull()
                    error = result.exceptionOrNull()?.message
                }, modifier = Modifier.fillMaxWidth()) { Text("Calcular") }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Text("Usa tablas LMS incluidas como recursos locales; no depende de internet.", style = MaterialTheme.typography.bodySmall)
            }
        }
        Column(Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            assessment?.let { a ->
                OutlinedCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Edad: ${a.ageText}", fontWeight = FontWeight.Bold)
                    Text("IMC: ${df.format(a.bmi)} kg/m² · ${a.nutritionSummary.label} · P${df.format(a.nutritionSummary.bmiPercentile)}")
                    Text("Sobrepeso desde: ${df.format(a.nutritionSummary.overweightFromKg)} kg · Obesidad desde: ${df.format(a.nutritionSummary.obesityFromKg)} kg")
                    a.measurementAdjustment?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
                    a.warnings.forEach { Text("• $it", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                } }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    a.results.forEach { r ->
                        FilterChip(selected = selectedChart == r, onClick = { selectedChart = r }, label = { Text(r.indicator.label) })
                    }
                }
                selectedChart?.let { result ->
                    OutlinedCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(result.indicator.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Valor: ${df.format(result.measuredValue)} ${result.indicator.unit} · Z ${df.format(result.zScore)} · P${df.format(result.percentile)}")
                        Text(result.interpretation)
                        GrowthChartCanvas(result.chart, Modifier.fillMaxWidth().height(360.dp))
                    } }
                }
            } ?: Text("Captura datos y calcula. La gráfica no va a adivinar al niño por telepatía, aunque sería útil.")
        }
    }
}

@Composable
private fun GrowthChartCanvas(chart: GrowthChart, modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    val outline = MaterialTheme.colorScheme.outline
    val error = MaterialTheme.colorScheme.error
    Canvas(modifier.background(MaterialTheme.colorScheme.surface).border(1.dp, outline, RoundedCornerShape(8.dp)).padding(8.dp)) {
        val allPoints = chart.curves.flatMap { it.points } + chart.patientPoint
        val minX = allPoints.minOf { it.x }
        val maxX = allPoints.maxOf { it.x }
        val minY = allPoints.minOf { it.y }
        val maxY = allPoints.maxOf { it.y }
        val left = 52f
        val top = 24f
        val right = size.width - 24f
        val bottom = size.height - 42f
        fun sx(x: Double): Float = left + ((x - minX) / max(0.0001, maxX - minX) * (right - left)).toFloat()
        fun sy(y: Double): Float = bottom - ((y - minY) / max(0.0001, maxY - minY) * (bottom - top)).toFloat()
        drawLine(outline, Offset(left, bottom), Offset(right, bottom), strokeWidth = 1.2f)
        drawLine(outline, Offset(left, top), Offset(left, bottom), strokeWidth = 1.2f)
        chart.curves.forEachIndexed { index, curve ->
            val path = Path()
            curve.points.forEachIndexed { pointIndex, point ->
                val px = sx(point.x)
                val py = sy(point.y)
                if (pointIndex == 0) path.moveTo(px, py) else path.lineTo(px, py)
            }
            drawPath(path, color = primary.copy(alpha = 0.35f + min(index, 3) * 0.12f), style = Stroke(width = 2f))
        }
        drawCircle(error, radius = 6f, center = Offset(sx(chart.patientPoint.x), sy(chart.patientPoint.y)))
    }
}

@Composable
private fun ObstetricsScreen() {
    var mode by remember { mutableStateOf("CRL") }
    var scanDate by remember { mutableStateOf(LocalDate.now().format(dateFormatter)) }
    var lmpDate by remember { mutableStateOf("") }
    var crl by remember { mutableStateOf("") }
    var trimester by remember { mutableStateOf(UltrasoundTrimester.SECOND) }
    var bpd by remember { mutableStateOf("") }
    var hc by remember { mutableStateOf("") }
    var ac by remember { mutableStateOf("") }
    var fl by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<UltrasoundDatingResult?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedCard(Modifier.width(380.dp).fillMaxHeight()) { Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Obstetricia", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = mode == "CRL", onClick = { mode = "CRL" }, label = { Text("1T LCC/CRL") })
                FilterChip(selected = mode == "BIO", onClick = { mode = "BIO" }, label = { Text("Biometría") })
            }
            OutlinedTextField(scanDate, { scanDate = it }, label = { Text("Fecha USG AAAA-MM-DD") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(lmpDate, { lmpDate = it }, label = { Text("FUM opcional AAAA-MM-DD") }, modifier = Modifier.fillMaxWidth())
            if (mode == "CRL") {
                OutlinedTextField(crl, { crl = it.numericText() }, label = { Text("LCC/CRL mm") }, modifier = Modifier.fillMaxWidth())
            } else {
                EnumMenu("Trimestre", trimester, listOf(UltrasoundTrimester.SECOND, UltrasoundTrimester.THIRD), { it.label }) { trimester = it }
                OutlinedTextField(bpd, { bpd = it.numericText() }, label = { Text("DBP/BPD mm") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(hc, { hc = it.numericText() }, label = { Text("CC/HC mm") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(ac, { ac = it.numericText() }, label = { Text("CA/AC mm") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(fl, { fl = it.numericText() }, label = { Text("LF/FL mm") }, modifier = Modifier.fillMaxWidth())
            }
            Button(onClick = {
                val calc = runCatching {
                    val lmp = lmpDate.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it, dateFormatter) }
                    val scan = LocalDate.parse(scanDate, dateFormatter)
                    if (mode == "CRL") {
                        UltrasoundDatingCalculator.fromCrl(scan, requireNotNull(crl.toDoubleOrNullMx()) { "Captura CRL." }, lmp).getOrThrow()
                    } else {
                        UltrasoundDatingCalculator.fromBiometry(scan, trimester, bpd.toDoubleOrNullMx(), hc.toDoubleOrNullMx(), ac.toDoubleOrNullMx(), fl.toDoubleOrNullMx(), lmp).getOrThrow()
                    }
                }
                result = calc.getOrNull(); error = calc.exceptionOrNull()?.message
            }, modifier = Modifier.fillMaxWidth()) { Text("Calcular") }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        } }
        ResultPane(Modifier.weight(1f).fillMaxHeight()) {
            result?.let { r ->
                Text(r.gestationalAgeLabel, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                Text("FPP estimada: ${r.estimatedDueDate}")
                Text("Método: ${r.method}")
                Text("Mediciones: ${r.measurementSummary}")
                Text("Precisión esperada: ${r.expectedAccuracy}")
                r.lmpComparison?.let { Text("Comparación FUM: ${it.message} Diferencia: ${it.differenceDays} días.") }
                r.warnings.forEach { Text("• $it", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } ?: Text("Sin resultado todavía.")
        }
    }
}

@Composable
private fun RenalScreen() {
    var method by remember { mutableStateOf(RenalMethod.CKD_EPI_2021_CREATININE) }
    var sex by remember { mutableStateOf(RenalSex.FEMALE) }
    var age by remember { mutableStateOf("") }
    var creatinine by remember { mutableStateOf("") }
    var creatinineUnit by remember { mutableStateOf(CreatinineUnit.MG_DL) }
    var cystatin by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var acr by remember { mutableStateOf("") }
    var acrUnit by remember { mutableStateOf(AcrUnit.MG_G) }
    var result by remember { mutableStateOf<RenalResult?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedCard(Modifier.width(420.dp).fillMaxHeight()) { Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Función renal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            EnumMenu("Método", method, RenalMethod.entries, { it.shortLabel }) { method = it }
            Text(method.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            EnumMenu("Sexo", sex, RenalSex.entries, { it.label }) { sex = it }
            OutlinedTextField(age, { age = it.filter(Char::isDigit) }, label = { Text("Edad años") }, modifier = Modifier.fillMaxWidth())
            if (method.requiresCreatinine) Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(creatinine, { creatinine = it.numericText() }, label = { Text("Creatinina") }, modifier = Modifier.weight(1f))
                EnumMenu("Unidad", creatinineUnit, CreatinineUnit.entries, { it.label }) { creatinineUnit = it }
            }
            if (method.requiresCystatinC) OutlinedTextField(cystatin, { cystatin = it.numericText() }, label = { Text("Cistatina C mg/L") }, modifier = Modifier.fillMaxWidth())
            if (method.requiresWeight) OutlinedTextField(weight, { weight = it.numericText() }, label = { Text("Peso kg") }, modifier = Modifier.fillMaxWidth())
            if (method.requiresHeight) OutlinedTextField(height, { height = it.numericText() }, label = { Text("Talla cm") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(acr, { acr = it.numericText() }, label = { Text("ACR opcional") }, modifier = Modifier.weight(1f))
                EnumMenu("Unidad", acrUnit, AcrUnit.entries, { it.label }) { acrUnit = it }
            }
            Button(onClick = {
                val calc = RenalCalculator.calculate(
                    RenalInput(
                        method = method,
                        ageYears = age.toIntOrNull() ?: 0,
                        sex = sex,
                        creatinine = creatinine.toDoubleOrNullMx(),
                        creatinineUnit = creatinineUnit,
                        cystatinCmgL = cystatin.toDoubleOrNullMx(),
                        weightKg = weight.toDoubleOrNullMx(),
                        heightCm = height.toDoubleOrNullMx(),
                        acr = acr.toDoubleOrNullMx(),
                        acrUnit = acrUnit
                    )
                )
                result = calc.getOrNull(); error = calc.exceptionOrNull()?.message
            }, modifier = Modifier.fillMaxWidth()) { Text("Calcular") }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        } }
        ResultPane(Modifier.weight(1f).fillMaxHeight()) {
            result?.let { r ->
                Text(r.primaryLabel, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Text("${df.format(r.primaryValue)} ${r.primaryUnit}", style = MaterialTheme.typography.headlineMedium)
                r.indexedValue?.let { Text("Indexado: ${df.format(it)} mL/min/1.73 m²") }
                r.bodySurfaceArea?.let { Text("SC Mosteller: ${df.format(it)} m²") }
                r.gfrCategory?.let { Text("Categoría G: ${it.label} · ${it.rangeLabel} · ${it.description}") }
                r.albuminuriaCategory?.let { Text("Albuminuria: ${it.label} · ${it.description}") }
                r.riskLevel?.let { Text("Riesgo G-A: ${it.label}", fontWeight = FontWeight.Bold) }
                r.warnings.forEach { Text("• $it", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } ?: Text("Sin resultado todavía.")
        }
    }
}

private sealed class UpdateUiState {
    data object Idle : UpdateUiState()
    data object Checking : UpdateUiState()
    data class Showing(val result: UpdateCheckResult) : UpdateUiState()
    data class Downloading(val fileName: String) : UpdateUiState()
    data class InstallerOpened(val path: String) : UpdateUiState()
    data class Error(val message: String) : UpdateUiState()
}

@Composable
private fun UpdatesScreen() {
    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf<UpdateUiState>(UpdateUiState.Idle) }

    ResultPane(Modifier.fillMaxSize()) {
        Text("Actualizaciones en Windows", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        Text("Versión instalada: ${AppBuildInfo.versionName}")
        Text("Repositorio de actualizaciones: ${AppBuildInfo.updateRepository}")
        Text("Carpeta de datos local: ${AppPaths.dataDir}")
        Text("Los medicamentos se guardan en medications.json y se pueden respaldar/importar con Excel/CSV/JSON.")

        if (!AppBuildInfo.updateRepositoryConfigured) {
            Text(
                "Todavía aparece el repositorio de ejemplo. Al compilar desde GitHub Actions se incrusta automáticamente el repositorio real.",
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            enabled = state !is UpdateUiState.Checking && state !is UpdateUiState.Downloading,
            onClick = {
                scope.launch {
                    state = UpdateUiState.Checking
                    val result = UpdateChecker.checkLatest()
                    state = result.fold(
                        onSuccess = { UpdateUiState.Showing(it) },
                        onFailure = { UpdateUiState.Error(it.message ?: "No se pudo buscar actualización.") }
                    )
                }
            }
        ) {
            Text("Buscar actualizaciones")
        }

        when (val current = state) {
            UpdateUiState.Idle -> Text("Presiona buscar para consultar el último instalador publicado en GitHub Releases.")
            UpdateUiState.Checking -> Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(Modifier.size(22.dp))
                Text("Consultando GitHub. La nube haciendo algo útil, esperemos.")
            }
            is UpdateUiState.Showing -> UpdateResultContent(
                result = current.result,
                onDownload = { info ->
                    scope.launch {
                        state = UpdateUiState.Downloading(info.fileName)
                        val result = WindowsUpdateInstaller.downloadAndOpen(info)
                        state = result.fold(
                            onSuccess = { UpdateUiState.InstallerOpened(it) },
                            onFailure = { UpdateUiState.Error(it.message ?: "No se pudo abrir el instalador.") }
                        )
                    }
                }
            )
            is UpdateUiState.Downloading -> Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(Modifier.size(22.dp))
                Text("Descargando ${current.fileName}...")
            }
            is UpdateUiState.InstallerOpened -> {
                Text("Instalador abierto correctamente.", fontWeight = FontWeight.Bold)
                Text("Archivo descargado: ${current.path}")
                Text("Cierra la app si el instalador lo pide. Sí, Windows todavía necesita ese ritual.")
            }
            is UpdateUiState.Error -> Text(current.message, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun UpdateResultContent(result: UpdateCheckResult, onDownload: (com.luisangel.calculadoramedicamentos.updater.UpdateInfo) -> Unit) {
    Text("Versión instalada: ${result.currentVersion}")
    Text("Última versión publicada: ${result.latestVersion}")
    Text("Notas: ${result.notes}")

    if (!result.updateAvailable) {
        Text("Ya tienes la versión más reciente.", fontWeight = FontWeight.Bold)
        return
    }

    Text("Hay una actualización disponible.", fontWeight = FontWeight.Bold)
    val info = result.updateInfo
    if (info == null) {
        Text("La versión existe, pero no encontré instalador .msi o .exe en el Release.", color = MaterialTheme.colorScheme.error)
    } else {
        Text("Instalador: ${info.fileName}")
        Button(onClick = { onDownload(info) }) {
            Text("Descargar e instalar")
        }
    }
}

@Composable
private fun ResultPane(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    OutlinedCard(modifier) {
        Column(Modifier.padding(18.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp), content = content)
    }
}

@Composable
private fun <T> EnumMenu(label: String, value: T, options: List<T>, text: (T) -> String, onSelected: (T) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) { Text("$label: ${text(value)}", maxLines = 1, overflow = TextOverflow.Ellipsis) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { item -> DropdownMenuItem(text = { Text(text(item)) }, onClick = { onSelected(item); expanded = false }) }
        }
    }
}

@Composable
private fun StringMenu(label: String, value: String, options: List<String>, text: (String) -> String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.widthIn(max = 190.dp)) { Text("$label: ${text(value)}", maxLines = 1, overflow = TextOverflow.Ellipsis) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { item -> DropdownMenuItem(text = { Text(text(item)) }, onClick = { onSelected(item); expanded = false }) }
        }
    }
}

@Composable
private fun SpecialtyFilterMenu(specialties: List<String>, selected: Set<String>, onSelected: (Set<String>) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) { Text(if (selected.isEmpty()) "Especialidades: todas" else "Especialidades: ${selected.size}") }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Limpiar selección") }, onClick = { onSelected(emptySet()); expanded = false })
            specialties.forEach { item ->
                DropdownMenuItem(
                    text = { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(item in selected, onCheckedChange = null); Text(item) } },
                    onClick = { onSelected(selected.toggle(item)) }
                )
            }
        }
    }
}

@Composable
private fun EditableSuggestionField(label: String, value: String, suggestions: List<String>, modifier: Modifier = Modifier, onValue: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier) {
        OutlinedTextField(value = value, onValueChange = onValue, label = { Text(label) }, modifier = Modifier.fillMaxWidth())
        IconButton(onClick = { expanded = true }, modifier = Modifier.align(Alignment.CenterEnd)) { Icon(Icons.Default.FilterAlt, null) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            suggestions.forEach { item -> DropdownMenuItem(text = { Text(item) }, onClick = { onValue(item); expanded = false }) }
        }
    }
}

private fun chooseImportFile(): File? {
    val chooser = JFileChooser().apply {
        dialogTitle = "Importar medicamentos"
        fileFilter = FileNameExtensionFilter("Excel, CSV o JSON", "xlsx", "xls", "csv", "json")
    }
    return if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) chooser.selectedFile else null
}

private fun chooseExportFile(): File? {
    val chooser = JFileChooser().apply {
        dialogTitle = "Exportar medicamentos"
        selectedFile = File("calculadora_medicamentos.xlsx")
        fileFilter = FileNameExtensionFilter("Excel XLSX", "xlsx")
    }
    return if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) chooser.selectedFile else null
}

private fun typeFilterLabel(value: TypeFilter): String = when (value) {
    TypeFilter.BOTH -> "Todos"
    TypeFilter.ADULT -> "Adultos"
    TypeFilter.SPECIAL_ADULT -> "Adultos especiales"
    TypeFilter.PEDIATRIC -> "Pediátricos"
}

private fun sortLabel(value: SortOption): String = when (value) {
    SortOption.NAME -> "Nombre"
    SortOption.CREATED_AT -> "Fecha alta"
    SortOption.FAMILY -> "Familia"
}

private fun recordTypeLabel(record: MedicationRecord): String = when {
    record.type == MedicationType.PEDIATRIC -> "Pediátrico"
    record.isSpecialAdult -> "Adulto especial"
    else -> "Adulto"
}

private fun doseWithUnit(record: MedicationRecord): String {
    val base = record.dose.ifBlank { record.dosePerKg?.let { "${df.format(it)} ${record.doseUnit}/kg" }.orEmpty() }
    return when {
        record.isInteractiveDose && record.dosePerKgMin != null && record.dosePerKgMax != null ->
            "$base · rango ${df.format(record.dosePerKgMin)}-${df.format(record.dosePerKgMax)} ${record.doseUnit}/kg"
        record.dosePerKg != null && (record.type == MedicationType.PEDIATRIC || record.isSpecialAdult) ->
            "$base · ${df.format(record.dosePerKg)} ${record.doseUnit}/kg"
        record.doseUnit.isNotBlank() && !base.contains(record.doseUnit, ignoreCase = true) -> "$base · ${record.doseUnit}"
        else -> base
    }
}

private fun String.numericText(): String {
    val normalized = replace(',', '.')
    val builder = StringBuilder()
    var dot = false
    normalized.forEach { char ->
        when {
            char.isDigit() -> builder.append(char)
            char == '.' && !dot -> { builder.append(char); dot = true }
        }
    }
    return builder.toString()
}

private fun String.toDoubleOrNullMx(): Double? = replace(',', '.').toDoubleOrNull()
private fun Set<String>.toggle(value: String): Set<String> = if (value in this) this - value else this + value
