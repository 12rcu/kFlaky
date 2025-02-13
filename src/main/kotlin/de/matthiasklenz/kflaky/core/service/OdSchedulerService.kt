package de.matthiasklenz.kflaky.core.service

import de.matthiasklenz.kflaky.adapters.plattform.Platform
import de.matthiasklenz.kflaky.core.execution.KFlakyExecutionRecipeTask
import de.matthiasklenz.kflaky.core.execution.RunType
import de.matthiasklenz.kflaky.core.execution.TestOrders
import de.matthiasklenz.kflaky.core.execution.WorkerPool
import de.matthiasklenz.kflaky.core.middleware.KFlakyLogger
import de.matthiasklenz.kflaky.core.project.ProjectInfo
import de.matthiasklenz.kflaky.core.strategy.TestExecutionStrategy
import de.matthiasklenz.kflaky.core.strategy.tuskanSquareOrderGenerate
import de.matthiasklenz.kflaky.core.tasks.TestFile
import de.matthiasklenz.kflaky.core.tasks.collectTestFiles
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.nio.file.Path
import kotlin.io.path.absolutePathString

class OdSchedulerService : KoinComponent {
    private val logger: KFlakyLogger by inject()
    private val testModificationService: TestModificationService by inject()

    suspend fun scheduleOdRuns(projectInfo: ProjectInfo, runId: Int, workerPool: WorkerPool) {
        val testFiles = testFiles(projectInfo)
        val orders = orders(projectInfo, testFiles)
        val testRuns = getTestRuns(projectInfo, orders)

        for (i in 0 until testRuns) {
            logger
                .get("OdRun Scheduler")
                .log("[${projectInfo.config.identifier}] Scheduled OD test execution ${i + 1}/$testRuns")
            val task = KFlakyExecutionRecipeTask(
                projectInfo,
                runId,
                i,
                RunType.OD,
                {
                    overwriteTestContent(projectInfo, orders, i, it)
                }
            ) { suite ->
                getTestOrderFor(projectInfo, orders, suite, i)
            }
            workerPool.execute(task)
        }
    }

    private fun getTestRuns(projectInfo: ProjectInfo, orders: List<TestOrders>): Int {
        if (projectInfo.config.strategy == TestExecutionStrategy.SKIP) return 0
        return orders.maxOf { o -> o.generatedOrders.size }
    }

    private fun testFiles(projectInfo: ProjectInfo) =
        collectTestFiles(initialTestSrcDir(projectInfo), projectInfo.config.frameworkConfig.testAnnotation).toList()

    private fun orders(projectInfo: ProjectInfo, testFiles: List<TestFile>): List<TestOrders> {
        return when (projectInfo.config.strategy) {
            TestExecutionStrategy.TUSCAN_SQUARES -> testFiles.map {
                val runOrders = tuskanSquareOrderGenerate(projectInfo.config.frameworkConfig, it.content)
                val orders =
                    testModificationService.modifyTestOrder(runOrders, it.content, projectInfo.config.frameworkConfig)
                val relativePath =
                    it.file.absolutePath.removePrefix(projectInfo.config.projectPath.absolutePathString())
                        .removePrefix(Platform.pathSeparator)
                TestOrders(relativePath, it.content, orders, runOrders)
            }.toList()

            else -> listOf()
        }
    }

    private fun initialTestSrcDir(projectInfo: ProjectInfo) = if (projectInfo.config.testSrcDir.isNotEmpty()) {
        projectInfo.config.projectPath.resolve(projectInfo.config.testSrcDir)
    } else {
        projectInfo.config.projectPath
    }

    private fun overwriteTestContent(
        projectInfo: ProjectInfo,
        orders: List<TestOrders>,
        index: Int,
        projectRootPath: Path
    ) {
        orders.forEach {
            testModificationService.writeTestOrder(
                projectInfo,
                it,
                projectRootPath.resolve(it.filePath).toFile(),
                index
            )
        }
    }

    private fun getTestOrderFor(
        projectInfo: ProjectInfo,
        orders: List<TestOrders>,
        testSuite: String,
        index: Int
    ): List<Int> {
        val order = orders.firstOrNull {
            projectInfo.config.frameworkConfig.isTestContentForTestSuite(
                it.content,
                testSuite
            )
        }
        return order?.orders?.matrix?.getOrNull(index) ?: listOf(-1)
    }
}