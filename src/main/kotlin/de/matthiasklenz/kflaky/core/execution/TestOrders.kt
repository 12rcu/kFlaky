package de.matthiasklenz.kflaky.core.execution

/**
 * @param filePath the relative path (from the project root) to the location of the test file
 * @param content the initial test file content (without any order annotation)
 * @param generatedOrders list of test file content with order annotations
 * @param orders info object about the orders generated for this specific test suite
 */
data class TestOrders(
    val filePath: String,
    val content: String,
    val generatedOrders: List<String>,
    val orders: TestOrderMatrix
)

interface TestOrderMatrix {
    val size: Int
    val matrix: List<List<Int>>
}