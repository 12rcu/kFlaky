package de.matthiasklenz.kflaky.core.execution

import de.matthiasklenz.kflaky.adapters.persistence.SqlLiteDB
import de.matthiasklenz.kflaky.core.project.ProjectConfig
import de.matthiasklenz.kflaky.core.project.ProjectProgress
import de.matthiasklenz.kflaky.core.project.ProjectState
import de.matthiasklenz.kflaky.core.strategy.TestExecutionStrategy
import de.matthiasklenz.kflaky.core.tasks.TestFile
import de.matthiasklenz.kflaky.core.tasks.collectTestFiles
import de.matthiasklenz.kflaky.core.tasks.modify.disableTestSuit
import de.matthiasklenz.kflaky.core.tasks.modify.tuskanSquareModify
import de.matthiasklenz.kflaky.core.tasks.modify.tuskanSquareOrderGenerate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.qualifier

class KFlakyTestExecutor(
    private val projectConfig: ProjectConfig,
    private val projectProgress: List<ProjectProgress>,
    private val runId: Int
) : KoinComponent {
    private val progressChannel: Channel<List<ProjectProgress>> by inject(qualifier("progress"))
    private val sqlLiteDB: SqlLiteDB by inject()
    private val progress = projectProgress.filter { it.name == projectConfig.identifier }
    private var orders: List<TestOrders> = listOf()
    private var testSuiteMostRuns = 0
    private val testCommand = OsCommand()
    private var testFiles = listOf<TestFile>()

    suspend fun runProject() = coroutineScope {
        progress.forEach { it.state = ProjectState.SETUP }
        progressChannel.send(projectProgress)

        println(progress.map { it.name })

        setup()
        runTestOrders()
        cleanup()

        progress.forEach { it.state = ProjectState.DONE }
        progressChannel.send(projectProgress)
    }

    private fun setup() {
        testFiles = collectTestFiles(projectConfig).toList()
        orders = when (projectConfig.strategy) {
            TestExecutionStrategy.TUSCAN_SQUARES -> testFiles.map {
                val runOrders = tuskanSquareOrderGenerate(projectConfig.framworkConfig, it.content)
                val orders = tuskanSquareModify(runOrders, it.content, projectConfig.framworkConfig)
                if (orders.size > testSuiteMostRuns) {
                    testSuiteMostRuns = orders.size
                }
                TestOrders(it.file, it.content, orders, runOrders)
            }.toList()

            else -> listOf()
        }
        progress.forEach { it.testsToRun = orders.maxOf { o -> o.generatedOrders.size } }
    }

    private suspend fun runTestOrders() {
        progress.forEach { it.state = ProjectState.OD_RUNS }
        progressChannel.send(projectProgress)

        val runs = orders.maxOf { it.generatedOrders.size }
        for (i in 0 until runs) {
            progress.forEach { it.index = i }
            progressChannel.send(projectProgress)
            overwriteTestContent(i)
            runTests()
            evaluate(i)
        }
        progress.forEach { it.index = it.testsToRun }
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

    private fun evaluate(index: Int) {
        projectConfig.testResultCollector.collect(projectConfig.testResultDir).forEach { result ->
            val order = orders.first { it.content.contains(result.testSuite) && it.content.contains(result.testName) }
            sqlLiteDB.addTestResult(
                runId,
                result,
                projectConfig.identifier,
                order.orders.matrix.getOrNull(index) ?: listOf(),
                state = ProjectState.OD_RUNS
            )
        }
    }

    private suspend fun cleanup() {
        progress.forEach { it.state = ProjectState.CLEANUP }
        progressChannel.send(projectProgress)
        testFiles.forEach {
            it.file.writeText(it.content)
        }
    }
}

