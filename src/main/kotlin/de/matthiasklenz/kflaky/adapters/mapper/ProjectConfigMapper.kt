package de.matthiasklenz.kflaky.adapters.mapper

import de.matthiasklenz.kflaky.adapters.jUnit.JUnitConfig
import de.matthiasklenz.kflaky.adapters.jUnit.JUnitResultCollection
import de.matthiasklenz.kflaky.adapters.project.ProjectConfigDto
import de.matthiasklenz.kflaky.core.project.ProjectConfig
import de.matthiasklenz.kflaky.core.project.TestFrameworkConfig
import de.matthiasklenz.kflaky.core.tasks.CollectResults
import java.nio.file.Path
import java.nio.file.Paths

fun ProjectConfigDto.map(): ProjectConfig {
    val language = TestFrameworkConfig.Language.valueOf(this.language.uppercase())
    val frameworkConfig: TestFrameworkConfig = when (this.framework.uppercase()) {
        "JUNIT" -> JUnitConfig(language)
        else -> throw NotImplementedError("This framework is currently not implemented!")
    }
    val collector: CollectResults = when (this.framework.uppercase()) {
        "JUNIT" -> JUnitResultCollection()
        else -> throw NotImplementedError("This framework is currently not implemented!")
    }

    val projectPath: Path = Paths.get(this.projectUri)
    return ProjectConfig(
        identifier,
        frameworkConfig,
        this.projectUri,
        projectPath,
        testExecutionDir,
        testResultDir,
        testDir,
        this.strategy,
        this.testExecutionCommand,
        collector,
        preRuns,
        enabled
    )
}