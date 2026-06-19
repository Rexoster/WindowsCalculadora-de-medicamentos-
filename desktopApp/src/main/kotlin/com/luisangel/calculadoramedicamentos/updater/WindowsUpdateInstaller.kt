package com.luisangel.calculadoramedicamentos.updater

import com.luisangel.calculadoramedicamentos.data.AppPaths
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.Duration
import java.util.Locale

object WindowsUpdateInstaller {
    private val client: HttpClient = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.ALWAYS)
        .connectTimeout(Duration.ofSeconds(20))
        .build()

    suspend fun downloadAndOpen(update: UpdateInfo): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            if (!System.getProperty("os.name").lowercase(Locale.ROOT).contains("windows")) {
                error("La instalación automática solo aplica en Windows.")
            }

            val updatesDir = AppPaths.dataDir.resolve("updates")
            Files.createDirectories(updatesDir)

            val safeName = update.fileName.replace(Regex("[^A-Za-z0-9._-]"), "_")
            val destination = updatesDir.resolve(safeName)
            val destinationFile = destination.toFile()

            val request = HttpRequest.newBuilder(URI.create(update.downloadUrl))
                .timeout(Duration.ofMinutes(10))
                .header("User-Agent", "CalculadoraMedicamentos-Windows-Updater")
                .GET()
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofInputStream())
            if (response.statusCode() !in 200..299) {
                error("No se pudo descargar el instalador. HTTP ${response.statusCode()}.")
            }

            response.body().use { input ->
                Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING)
            }

            ProcessBuilder("cmd", "/c", "start", "", destinationFile.absolutePath)
                .directory(AppPaths.dataDir.toFile())
                .start()

            destinationFile.absolutePath
        }
    }
}
