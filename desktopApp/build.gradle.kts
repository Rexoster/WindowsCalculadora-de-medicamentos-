import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.compose")
}

group = "com.luisangel.calculadoramedicamentos"

val defaultVersionName = "3.1.0"
val desktopVersionName = providers.environmentVariable("APP_VERSION_NAME")
    .orElse(defaultVersionName)
    .get()
val updateRepositoryName = providers.environmentVariable("APP_UPDATE_REPOSITORY")
    .orElse("TU_USUARIO/TU_REPOSITORIO")
    .get()

version = "$desktopVersionName-windows"

val generatedAppBuildInfoDir = layout.buildDirectory.dir("generated/resources/appBuildInfo")

val generateAppBuildInfo by tasks.registering {
    outputs.dir(generatedAppBuildInfoDir)

    doLast {
        val outputDir = generatedAppBuildInfoDir.get().asFile
        outputDir.mkdirs()
        outputDir.resolve("app-build.properties").writeText(
            """
            versionName=$desktopVersionName
            updateRepository=$updateRepositoryName
            platform=windows
            """.trimIndent() + "\n"
        )
    }
}

sourceSets {
    main {
        resources.srcDir(generatedAppBuildInfoDir)
    }
}

tasks.named("processResources") {
    dependsOn(generateAppBuildInfo)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    implementation("org.apache.poi:poi:5.4.1")
    implementation("org.apache.poi:poi-ooxml:5.4.1")
}

compose.desktop {
    application {
        mainClass = "com.luisangel.calculadoramedicamentos.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Msi)
            packageName = "CalculadoraMedicamentos"
            packageVersion = desktopVersionName
            description = "Calculadora de medicamentos, percentiles, obstetricia y función renal para Windows."
            copyright = "Luis Angel"

            modules(
                "java.desktop",
                "java.logging",
                "java.management",
                "java.naming",
                "java.sql",
                "java.xml",
                "jdk.unsupported"
            )

            windows {
                menuGroup = "Calculadora de Medicamentos"
                shortcut = true
                dirChooser = true
                upgradeUuid = "8d4db2fb-9c68-4e10-93c7-3fd7e6a34c9d"
            }
        }
    }
}
