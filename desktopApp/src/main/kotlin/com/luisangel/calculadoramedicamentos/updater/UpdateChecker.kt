package com.luisangel.calculadoramedicamentos.updater

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.Locale

object UpdateChecker {
    private val client: HttpClient = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.ALWAYS)
        .connectTimeout(Duration.ofSeconds(20))
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun checkLatest(): Result<UpdateCheckResult> = withContext(Dispatchers.IO) {
        runCatching {
            if (!AppBuildInfo.updateRepositoryConfigured) {
                error("El repositorio de actualizaciones aún no está configurado. Compila desde GitHub Actions para que se incruste automáticamente.")
            }

            val releases = getJsonArray("https://api.github.com/repos/${AppBuildInfo.updateRepository}/releases?per_page=20")
            val release = releases.firstObjectOrNull { item -> item.hasWindowsUpdateAsset() }
                ?: error("No encontré un Release de Windows con update-windows.json, .msi o .exe.")
            val tagVersion = release.string("tag_name") ?: error("GitHub no devolvió etiqueta de versión.")
            val releaseUrl = release.string("html_url") ?: "https://github.com/${AppBuildInfo.updateRepository}/releases"
            val assets = release["assets"]?.jsonArray ?: JsonArray(emptyList())

            val manifestAsset = assets.firstObjectOrNull { it.string("name") == "update-windows.json" }
            val manifest = manifestAsset?.string("browser_download_url")?.let { getJsonObject(it) }

            val latestVersion = manifest?.string("versionName") ?: tagVersion.cleanVersion()
            val notes = manifest?.string("notes")
                ?: release.string("body")
                ?: "Sin notas de versión. Misterio clásico de los releases."

            val installerFromManifest = manifest?.let {
                val url = it.string("downloadUrl")
                val fileName = it.string("fileName")
                if (!url.isNullOrBlank() && !fileName.isNullOrBlank()) {
                    UpdateInfo(
                        versionName = latestVersion,
                        fileName = fileName,
                        downloadUrl = url,
                        releaseUrl = it.string("releaseUrl") ?: releaseUrl,
                        notes = notes
                    )
                } else null
            }

            val installer = installerFromManifest ?: assets
                .mapNotNull { asset ->
                    val obj = asset.jsonObject
                    val name = obj.string("name") ?: return@mapNotNull null
                    val url = obj.string("browser_download_url") ?: return@mapNotNull null
                    val lower = name.lowercase(Locale.ROOT)
                    if (lower.endsWith(".msi") || lower.endsWith(".exe")) {
                        UpdateInfo(
                            versionName = latestVersion,
                            fileName = name,
                            downloadUrl = url,
                            releaseUrl = releaseUrl,
                            notes = notes
                        )
                    } else null
                }
                .sortedBy { if (it.fileName.lowercase(Locale.ROOT).endsWith(".msi")) 0 else 1 }
                .firstOrNull()

            val comparison = compareVersions(AppBuildInfo.versionName, latestVersion)
            UpdateCheckResult(
                currentVersion = AppBuildInfo.versionName,
                latestVersion = latestVersion,
                updateAvailable = comparison < 0,
                updateInfo = installer,
                releaseUrl = releaseUrl,
                notes = notes
            )
        }
    }

    private fun getJsonObject(url: String): JsonObject {
        val element = getJsonElement(url)
        return element.jsonObject
    }

    private fun getJsonArray(url: String): JsonArray {
        val element = getJsonElement(url)
        return element.jsonArray
    }

    private fun getJsonElement(url: String): kotlinx.serialization.json.JsonElement {
        val request = HttpRequest.newBuilder(URI.create(url))
            .timeout(Duration.ofSeconds(30))
            .header("Accept", "application/vnd.github+json")
            .header("User-Agent", "CalculadoraMedicamentos-Windows-Updater")
            .GET()
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() !in 200..299) {
            error("GitHub respondió HTTP ${response.statusCode()}. Si el repositorio es privado, la app no puede consultar actualizaciones sin autenticación.")
        }
        return json.parseToJsonElement(response.body())
    }

    private fun JsonObject.hasWindowsUpdateAsset(): Boolean {
        val tag = string("tag_name")?.lowercase(Locale.ROOT).orEmpty()
        val assets = this["assets"]?.jsonArray ?: return tag.startsWith("windows-v")
        return tag.startsWith("windows-v") || assets.any { asset ->
            val name = asset.jsonObject.string("name")?.lowercase(Locale.ROOT).orEmpty()
            name == "update-windows.json" || name.endsWith(".msi") || name.endsWith(".exe")
        }
    }

    private fun JsonObject.string(key: String): String? = this[key]?.jsonPrimitive?.contentOrNull

    private inline fun JsonArray.firstObjectOrNull(predicate: (JsonObject) -> Boolean): JsonObject? {
        for (item in this) {
            val obj = item.jsonObject
            if (predicate(obj)) return obj
        }
        return null
    }

    private fun String.cleanVersion(): String = trim()
        .removePrefix("windows-")
        .removePrefix("v")
        .removePrefix("V")

    private fun compareVersions(current: String, latest: String): Int {
        val a = current.cleanVersion().splitToVersionInts()
        val b = latest.cleanVersion().splitToVersionInts()
        val max = maxOf(a.size, b.size)
        for (index in 0 until max) {
            val left = a.getOrElse(index) { 0 }
            val right = b.getOrElse(index) { 0 }
            if (left != right) return left.compareTo(right)
        }
        return 0
    }

    private fun String.splitToVersionInts(): List<Int> = split('.', '-', '_')
        .mapNotNull { part -> part.takeWhile(Char::isDigit).takeIf(String::isNotBlank)?.toIntOrNull() }
        .ifEmpty { listOf(0) }
}

data class UpdateCheckResult(
    val currentVersion: String,
    val latestVersion: String,
    val updateAvailable: Boolean,
    val updateInfo: UpdateInfo?,
    val releaseUrl: String,
    val notes: String
)

data class UpdateInfo(
    val versionName: String,
    val fileName: String,
    val downloadUrl: String,
    val releaseUrl: String,
    val notes: String
)
