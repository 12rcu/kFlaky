package de.matthiasklenz.kflaky.adapters.mapper

import de.matthiasklenz.kflaky.adapters.jUnit.JUnitConfig
import de.matthiasklenz.kflaky.adapters.jUnit.JUnitResultCollection
import de.matthiasklenz.kflaky.adapters.project.ProjectConfigDto
import de.matthiasklenz.kflaky.core.project.ProjectConfig
import de.matthiasklenz.kflaky.core.project.TestFrameworkConfig
import de.matthiasklenz.kflaky.core.tasks.CollectResults
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.Path

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

    val projectPath: Path = try {
        Paths.get(this.projectUri)
    } catch (e: InvalidPathException) {
        Path("/not/extend/folder/path/file/whatever")    //try set path later
    }
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