package de.matthiasklenz.kflaky.core.tasks

import de.matthiasklenz.kflaky.core.execution.RunType
import java.nio.file.Path

interface CollectResults {
    fun collect(resultsPath: Path, getTestOrderOf: (testSuiteId: String) -> List<Int>): List<TestSuiteResultInfo>
}

data class TestResultData(
    val testName: String,
    val outcome: TestOutcome
)

data class TestSuiteResultInfo(
    val name: String,
    val raw: String,
    val order: List<Int>,
    val testResults: MutableList<TestResultData>
)

data class TestOutcomeInfo(
    val runId: Int,
    val iteration: Int,
    val runType: RunType,
    val testSuiteResults: List<TestSuiteResultInfo>
)

enum class TestOutcome {
    PASSED,
    SKIPPED,
    FAILED,
    ERROR,
    TIMEOUT
}