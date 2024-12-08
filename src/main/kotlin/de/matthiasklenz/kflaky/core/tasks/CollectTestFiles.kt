package de.matthiasklenz.kflaky.core.tasks

import de.matthiasklenz.kflaky.core.project.ProjectConfig
import java.io.File
import kotlin.io.path.walk

fun collectTestFiles(projectConfig: ProjectConfig): Sequence<TestFile> {
    return projectConfig.testSrcDir.walk().filter {
        it.toFile().isFile && it.toFile().readText().contains(projectConfig.framworkConfig.testAnnotation)
    }.map {
        TestFile(
            content = it.toFile().readText(),
            file = it.toFile()
        )
    }
}

data class TestFile(
    val content: String,
    val file: File
)