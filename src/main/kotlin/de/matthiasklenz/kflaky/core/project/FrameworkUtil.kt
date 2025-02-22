package de.matthiasklenz.kflaky.core.project

interface FrameworkUtil {
    fun getTesFileInfo(fileName: String, content: String):  Result<FrameworkTestFileContent>
}

/**
 * @param classContent all class names of this test file. Key is the index of the test class name
 */
data class FrameworkTestFileContent(
    val classContent: HashMap<Int, FrameWorkTestSuiteData>
)

/**
 * @param suiteName the class name
 * @param testNames all test names
 */
data class FrameWorkTestSuiteData(
    val suiteName: String,
    val testNames: HashMap<Int, String>,
)