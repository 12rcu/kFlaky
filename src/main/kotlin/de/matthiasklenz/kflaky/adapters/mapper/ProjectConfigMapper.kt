package de.matthiasklenz.kflaky.adapters.mapper

import de.matthiasklenz.kflaky.adapters.jUnit.JUnitConfig
import de.matthiasklenz.kflaky.adapters.jUnit.JUnitResultCollection
import de.matthiasklenz.kflaky.adapters.project.ProjectConfigDto
import de.matthiasklenz.kflaky.core.project.ProjectConfig
import de.matthiasklenz.kflaky.core.project.TestFramworkConfig
import de.matthiasklenz.kflaky.core.tasks.CollectResults
import java.nio.file.Path
import java.nio.file.Paths

fun ProjectConfigDto.map(): ProjectConfig {
    val language = TestFramworkConfig.Language.valueOf(this.language.uppercase())
    val frameworkConfig: TestFramworkConfig = when(this.framework.uppercase()) {
        "JUNIT" -> JUnitConfig(language)
        else -> throw NotImplementedError("This framework is currently not implemented!")
    }
    val collector: CollectResults = when(this.framework.uppercase()) {
        "JUNIT" -> JUnitResultCollection()
        else -> throw NotImplementedError("This framework is currently not implemented!")
    }

    return ProjectConfig(
        identifier,
        frameworkConfig,
        Paths.get(this.testResultDir),
        Paths.get(this.testDir),
        this.strategy,
        this.testExecutionCommand,
        Paths.get(this.testExecutionDir),
        collector,
        preRuns
    )
}