package de.matthiasklenz.kflaky

import de.matthiasklenz.kflaky.adapters.project.ProjectConfigDto
import de.matthiasklenz.kflaky.core.project.ProjectConfig
import kotlinx.serialization.Serializable
import java.nio.file.Path

@Serializable
data class KFlakyConfigDto(
    val baseDir: String,
    val logDir: String = "log",
    val tmpDir: String = "tmp",
    val worker: Int = 8,
    val projects: List<ProjectConfigDto>
)

data class KFlakyConfig(
    val baseDir: Path,
    val logDir: Path,
    val tmpDir: Path,
    val worker: Int,
    val projects: List<ProjectConfig>
)