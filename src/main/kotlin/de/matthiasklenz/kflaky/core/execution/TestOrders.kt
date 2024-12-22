package de.matthiasklenz.kflaky.core.execution

import de.matthiasklenz.kflaky.core.strategy.tuscansq.TuscanCalculation
import java.io.File

/**
 * @param filePath the relative path (from the project root) to the location of the test file
 * @param content the inital test file content (witjout any order annotation)
 * @param generatedOrders list of test file content with order annotations
 * @param orders info object about the orderes generated for this specifc test suite
 */
data class TestOrders(
    val filePath: String,
    val content: String,
    val generatedOrders: List<String>,
    val orders: TuscanCalculation
)