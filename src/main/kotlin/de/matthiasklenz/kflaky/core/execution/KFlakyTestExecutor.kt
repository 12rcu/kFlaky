package de.matthiasklenz.kflaky.core.execution

import de.matthiasklenz.kflaky.core.project.ProjectConfig
import de.matthiasklenz.kflaky.core.strategy.TestExecutionStrategy
import de.matthiasklenz.kflaky.core.tasks.collectTestFiles
import de.matthiasklenz.kflaky.core.tasks.modify.disableTestSuit
import de.matthiasklenz.kflaky.core.tasks.modify.tuskanSquareModify

class KFlakyTestExecutor(private val projectConfig: ProjectConfig) {
    private var orders: List<TestOrders> = listOf()
    private var testSuiteMostRuns = 0

    fun runProject() {
        setup()
        runTests()
        cleanup()
    }

    private fun setup() {
        val testFiles = collectTestFiles(projectConfig)
        orders = when(projectConfig.strategy) {
            TestExecutionStrategy.TUSCAN_SQUARES -> testFiles.map {
                val orders = tuskanSquareModify(projectConfig.framworkConfig, it.content)
                if(orders.size > testSuiteMostRuns) {
                    testSuiteMostRuns = orders.size
                }
                TestOrders(it.file, it.content, orders)
            }.toList()
            else -> listOf()
        }
    }

    private fun runTestOrders() {
        for (i in 0 until testSuiteMostRuns) {
            overwriteTestContent(i)
            runTests()
            evaluate()
        }
    }

    private fun overwriteTestContent(index: Int) {
        orders.forEach {
            val order = it.generatedOrders.getOrNull(index)
            if(order == null) {
                it.file.writeText(disableTestSuit(projectConfig.framworkConfig, it.content))
                return
            } else {
                it.file.writeText(order)
            }
        }
    }

    private fun runTests() {

    }

    private fun evaluate() {

    }

    private fun cleanup() {

    }
}

