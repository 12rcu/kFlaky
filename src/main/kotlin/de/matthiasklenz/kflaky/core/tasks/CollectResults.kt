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

/**
 * @param name the name of the test suite
 * @param raw the raw content of which this object was created
 * @param order the order that was used to generate this test
 * @param testIdIndex the index on which the test was run, to identify the test in the order list
 * @param testResults the name of test with its result
 */
data class TestSuiteResultInfo(
    val name: String,
    val raw: String,
    val order: List<Int>,
    val testIdIndex: HashMap<Int, String> = hashMapOf(),
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