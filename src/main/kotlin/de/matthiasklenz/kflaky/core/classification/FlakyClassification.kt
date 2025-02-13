package de.matthiasklenz.kflaky.core.classification

enum class FlakyClassification {
    NON_FLAKY,
    OD_FLAKY,
    OTHER_FLAKY
}

data class TestClassificationInfo(
    val testName: String,
    val testSuite: String,
    val outcome: FlakyClassification,
    var minimizedTestPass: MinimizedTestInfo? = null,
    var maximizedTestFail: MinimizedTestInfo? = null,
)

data class MinimizedTestInfo(
    val order: List<Int>,
    val fileContent: String,
)