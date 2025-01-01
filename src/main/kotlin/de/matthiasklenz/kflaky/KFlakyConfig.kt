package de.matthiasklenz.kflaky

import de.matthiasklenz.kflaky.adapters.mapper.map
import de.matthiasklenz.kflaky.adapters.project.ProjectConfigDto
import de.matthiasklenz.kflaky.core.project.ProjectConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.Path

@Serializable
data class KFlakyConfigDto(
    val baseDir: String,
    val logDir: String = "log",
    val tmpDir: String = "tmp",
    val worker: Int = 8,
    val projects: List<ProjectConfigDto>
)

fun loadConfig(path: Path): KFlakyConfig {
    val config = path.toFile().readText()
    return Json.decodeFromString<KFlakyConfigDto>(config).map()
}

data class KFlakyConfig(
    val baseDir: Path,
    val logDir: Path,
    val tmpDir: Path,
    val worker: Int,
    val projects: List<ProjectConfig>
)