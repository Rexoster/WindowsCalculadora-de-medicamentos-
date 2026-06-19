package com.luisangel.calculadoramedicamentos.updater

import java.util.Properties

object AppBuildInfo {
    private val properties: Properties by lazy {
        Properties().also { props ->
            val stream = javaClass.classLoader.getResourceAsStream("app-build.properties")
            if (stream != null) stream.use(props::load)
        }
    }

    val versionName: String
        get() = properties.getProperty("versionName")?.takeIf { it.isNotBlank() } ?: "3.1.0"

    val updateRepository: String
        get() = properties.getProperty("updateRepository")?.takeIf { it.isNotBlank() } ?: "TU_USUARIO/TU_REPOSITORIO"

    val platform: String
        get() = properties.getProperty("platform")?.takeIf { it.isNotBlank() } ?: "windows"

    val updateRepositoryConfigured: Boolean
        get() = updateRepository != "TU_USUARIO/TU_REPOSITORIO" && "/" in updateRepository
}
