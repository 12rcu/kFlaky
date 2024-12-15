package de.matthiasklenz.kflaky.core.tasks

import java.nio.file.Path

interface CollectResults {
    fun collect(resultsPath: Path): List<TestResultData>
}

data class TestResultData(
    val testSuite: String,
    val testName: String,
    val outcome: TestOutcome
)

enum class TestOutcome {
    PASSED,
    SKIPPED,
    FAILED,
    ERROR,
    TIMEOUT
}