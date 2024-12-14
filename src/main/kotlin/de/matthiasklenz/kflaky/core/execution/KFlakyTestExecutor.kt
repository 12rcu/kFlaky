package de.matthiasklenz.kflaky.core.execution

import de.matthiasklenz.kflaky.core.project.ProjectConfig
import de.matthiasklenz.kflaky.core.strategy.TestExecutionStrategy
import de.matthiasklenz.kflaky.core.tasks.TestFile
import de.matthiasklenz.kflaky.core.tasks.collectTestFiles
import de.matthiasklenz.kflaky.core.tasks.modify.disableTestSuit
import de.matthiasklenz.kflaky.core.tasks.modify.tuskanSquareModify
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class KFlakyTestExecutor(private val projectConfig: ProjectConfig, private val outputChannel: Channel<String>) {
    private var orders: List<TestOrders> = listOf()
    private var testSuiteMostRuns = 0
    private val testCommand = OsCommand(outputChannel)
    private var testFiles = listOf<TestFile>()

    suspend fun runProject() = coroutineScope {
        launch {
            for (o in outputChannel) {
                println("DEBUG: $o")
            }
        }

        setup()
        runTestOrders()
        cleanup()

        outputChannel.close()
    }

    private fun setup() {
        testFiles = collectTestFiles(projectConfig).toList()
        orders = when (projectConfig.strategy) {
            TestExecutionStrategy.TUSCAN_SQUARES -> testFiles.map {
                val orders = tuskanSquareModify(projectConfig.framworkConfig, it.content)
                if (orders.size > testSuiteMostRuns) {
                    testSuiteMostRuns = orders.size
                }
                TestOrders(it.file, it.content, orders)
            }.toList()

            else -> listOf()
        }
    }

    private suspend fun runTestOrders() {
        val runs = orders.maxOf { it.generatedOrders.size }
        for (i in 0 until runs) {
            overwriteTestContent(i)
            runTests()
            evaluate()
        }
    }

    private fun overwriteTestContent(index: Int) {
        orders.forEach {
            val order = it.generatedOrders.getOrNull(index)
            if (order == null) {
                it.file.writeText(disableTestSuit(projectConfig.framworkConfig, it.content))
                return
            } else {
                it.file.writeText(order)
            }
        }
    }

    private suspend fun runTests() {
        testCommand.executeTestCommand(projectConfig.testCommand, projectConfig.testExecutionPath.toFile())
    }

    private fun evaluate() {
        val results = projectConfig.testResultCollector.collect(projectConfig.testResultDir)
        println("Results: ${results.map { "name: ${it.testName} -> ${it.outcome}" }}")
    }

    private fun cleanup() {
        testFiles.forEach {
            it.file.writeText(it.content)
        }
    }
}

