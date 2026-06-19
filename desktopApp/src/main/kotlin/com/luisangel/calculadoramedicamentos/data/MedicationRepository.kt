package com.luisangel.calculadoramedicamentos.data

import com.luisangel.calculadoramedicamentos.model.MedicationRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.nio.file.StandardCopyOption
import java.util.Locale
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

class MedicationRepository(
    private val file: java.nio.file.Path = AppPaths.medicationsFile
) {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }
    private val serializer = ListSerializer(MedicationRecord.serializer())
    private val _medications = MutableStateFlow(loadRecords())
    val medications: StateFlow<List<MedicationRecord>> = _medications

    suspend fun getAll(): List<MedicationRecord> = _medications.value

    suspend fun upsert(record: MedicationRecord) {
        val current = _medications.value.toMutableList()
        val index = current.indexOfFirst { it.id == record.id }
        if (index >= 0) current[index] = record else current += record
        persist(current.sortedForUi())
    }

    suspend fun upsertAll(records: List<MedicationRecord>) {
        if (records.isEmpty()) return
        val byId = _medications.value.associateBy { it.id }.toMutableMap()
        records.forEach { byId[it.id] = it }
        persist(byId.values.toList().sortedForUi())
    }

    suspend fun delete(id: String) {
        persist(_medications.value.filterNot { it.id == id })
    }

    suspend fun deleteAll() {
        persist(emptyList())
    }

    suspend fun count(): Int = _medications.value.size

    @Synchronized
    private fun loadRecords(): List<MedicationRecord> {
        return runCatching {
            if (!file.exists()) return emptyList()
            file.inputStream().use { input ->
                json.decodeFromString(serializer, input.bufferedReader().readText())
            }.sortedForUi()
        }.getOrElse { emptyList() }
    }

    @Synchronized
    private fun persist(records: List<MedicationRecord>) {
        file.parent?.createDirectories()
        val temporary = file.resolveSibling(file.fileName.toString() + ".tmp")
        temporary.outputStream().bufferedWriter().use { writer ->
            writer.write(json.encodeToString(serializer, records))
        }
        runCatching {
            java.nio.file.Files.move(
                temporary,
                file,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
            )
        }.getOrElse {
            java.nio.file.Files.move(
                temporary,
                file,
                StandardCopyOption.REPLACE_EXISTING
            )
        }
        _medications.value = records
    }

    private fun List<MedicationRecord>.sortedForUi(): List<MedicationRecord> =
        sortedWith(compareBy<MedicationRecord> { it.name.lowercase(Locale.ROOT) }.thenBy { it.presentation.lowercase(Locale.ROOT) })
}
