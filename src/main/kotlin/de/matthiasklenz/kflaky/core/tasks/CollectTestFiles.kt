package de.matthiasklenz.kflaky.core.tasks

import java.io.File
import java.nio.file.Path
import kotlin.io.path.walk

fun collectTestFiles(testDir: Path, testAnnotation: Regex): Sequence<TestFile> {
    return testDir.walk().filter {
        it.toFile().isFile && it.toFile().readText().contains(testAnnotation)
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