package de.matthiasklenz.kflaky.core.execution

import de.matthiasklenz.kflaky.adapters.plattform.Plattform
import de.matthiasklenz.kflaky.core.pool.WorkerPool
import de.matthiasklenz.kflaky.core.project.ProjectConfig
import de.matthiasklenz.kflaky.core.project.ProjectProgress
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
import java.lang.Exception
import java.nio.file.Path
import kotlin.io.path.absolutePathString

class KFlakyOdTestExecutor(
    private val projectConfig: ProjectConfig,
    private val projectProgress: ProjectProgress,
    private val runId: Int
) : KoinComponent {
    private val progressChannel: Channel<ProjectProgress> by inject(qualifier("progress"))
    private val terminalLog: Channel<String> by inject(qualifier("terminal"))
    private val workerPool: WorkerPool by inject()
    private var testFiles = listOf<TestFile>()
    private val initialTestSrcDir = if (projectConfig.testSrcDir.isNotEmpty()) {
        projectConfig.projectPath.resolve(projectConfig.testSrcDir)
    } else {
        projectConfig.projectPath
    }

    suspend fun runProject() = coroutineScope {
        if(projectConfig.strategy == TestExecutionStrategy.SKIP)
            return@coroutineScope
        progressChannel.send(projectProgress)
        //generate orders from initial test src dir
        if (orders.isNotEmpty()) {
            runTestOrders()
        }
    }

    val testRuns: Int by lazy {
        orders.maxOf { o -> o.generatedOrders.size }
    }

    /**
     * Generates the test order if it hasn't happend before.
     * Uses the inital project directory to generate instaed of worker copies
     */
    private val orders: List<TestOrders> by lazy {
        testFiles = collectTestFiles(initialTestSrcDir, projectConfig.framworkConfig.testAnnotation).toList()
        when (projectConfig.strategy) {
            TestExecutionStrategy.TUSCAN_SQUARES -> testFiles.map {
                val runOrders = tuskanSquareOrderGenerate(projectConfig.framworkConfig, it.content)
                val orders = tuskanSquareModify(runOrders, it.content, projectConfig.framworkConfig)
                val relativePath = it.file.absolutePath.removePrefix(projectConfig.projectPath.absolutePathString())
                    .removePrefix(Plattform.pathSeperator)
                TestOrders(relativePath, it.content, orders, runOrders)
            }.toList()

            else -> listOf()
        }
    }

    private suspend fun runTestOrders() {
        for (i in 0 until testRuns) {
            terminalLog.send("[${projectConfig.identifier}] Scheduled OD test execution ${i + 1}/$testRuns")
            val task = KFlakyExecutionRecipeTask(
                projectConfig.identifier,
                projectProgress,
                runId,
                {
                    overwriteTestContent(i, it)
                }
            ) { suite, test ->
                getTestOrderFor(suite, test, i)
            }
            workerPool.execute(task)
        }
    }

    private fun overwriteTestContent(index: Int, projectRootPath: Path) {
        orders.forEach {
            val order = it.generatedOrders.getOrNull(index)
            val testFile = projectRootPath.resolve(it.filePath).toFile()

            if (order == null) {
                testFile.writeText(disableTestSuit(projectConfig.framworkConfig, it.content))
            } else {
                try {
                    testFile.writeText(order)
                } catch (e: Exception) {
                    println("Root path: $projectRootPath")
                    println("Test file: ${it.filePath}")
                    println(testFile.absolutePath)
                    e.printStackTrace()
                    throw Exception("stop")
                }
            }
        }
    }

    private fun getTestOrderFor(testSuite: String, testName: String, index: Int): List<Int> {
        val order = orders.firstOrNull {
            projectConfig.framworkConfig.isTestContentForTestSuite(
                it.content,
                testSuite,
                testName
            )
        }
        return order?.orders?.matrix?.getOrNull(index) ?: listOf()
    }
}

