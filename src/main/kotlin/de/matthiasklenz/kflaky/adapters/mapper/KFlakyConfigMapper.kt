package de.matthiasklenz.kflaky.adapters.mapper

import de.matthiasklenz.kflaky.KFlakyConfig
import de.matthiasklenz.kflaky.KFlakyConfigDto
import kotlin.io.path.Path

fun KFlakyConfigDto.map(): KFlakyConfig {
    val basePath = Path(baseDir)
    basePath.toFile().mkdirs()

    return KFlakyConfig(
        basePath,
        basePath.resolve(logDir),
        basePath.resolve(tmpDir),
        worker,
        projects.map { it.map() },
        githubQuery
    )
}