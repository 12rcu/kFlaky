package de.matthiasklenz.kflaky.adapters.mapper

import de.matthiasklenz.kflaky.adapters.jUnit.JUnitConfig
import de.matthiasklenz.kflaky.adapters.project.ProjectConfigDto
import de.matthiasklenz.kflaky.core.project.ProjectConfig
import de.matthiasklenz.kflaky.core.project.TestFramworkConfig
import java.nio.file.Paths

fun ProjectConfigDto.map(): ProjectConfig {
    val language = TestFramworkConfig.Language.valueOf(this.language.uppercase())
    val frameworkConfig: TestFramworkConfig = when(this.framework.uppercase()) {
        "JUNIT" -> JUnitConfig(language)
        else -> throw NotImplementedError("This framework is currently not implemented!")
    }

    return ProjectConfig(
        frameworkConfig,
        Paths.get("").resolve(this.testResultDir),
        Paths.get("").resolve(this.testDir),
        this.strategy
    )
}