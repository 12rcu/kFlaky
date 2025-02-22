package de.matthiasklenz.kflaky.core.service

import de.matthiasklenz.kflaky.core.exceptions.MissingFrameworkPart
import de.matthiasklenz.kflaky.core.execution.ExecutionTask
import de.matthiasklenz.kflaky.core.execution.KFlakyExecutionRecipeTask
import de.matthiasklenz.kflaky.core.execution.PairWiseExecutionTask
import de.matthiasklenz.kflaky.core.execution.RunType
import de.matthiasklenz.kflaky.core.execution.WorkerPool
import de.matthiasklenz.kflaky.core.middleware.KFlakyLogger
import de.matthiasklenz.kflaky.core.project.FrameworkTestFileContent
import de.matthiasklenz.kflaky.core.project.ProjectInfo
import de.matthiasklenz.kflaky.core.project.ProjectState
import de.matthiasklenz.kflaky.core.tasks.collectTestFiles
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

class PairWiseSchedulerService : KoinComponent {
    private val logger: KFlakyLogger by inject()

    suspend fun schedulePairWiseRuns(projectInfo: ProjectInfo, runId: Int, workerPool: WorkerPool) {
        val log = logger.get("PairWiseSchedulerService")
        projectInfo.progress.state = ProjectState.OD_RUNS

        val util = projectInfo.config.frameworkConfig.frameworkUtil
            ?: throw MissingFrameworkPart("No framework util defined for class: ${projectInfo.config.frameworkConfig::class}")

        val testPath =
            if (projectInfo.config.testSrcDir == "")
                projectInfo.config.projectPath
            else
                projectInfo.config.projectPath.resolve(projectInfo.config.testSrcDir)
        val testFiles = collectTestFiles(testPath, projectInfo.config.frameworkConfig.testAnnotation)

        val tests = testFiles.map { testFile ->
            util.getTesFileInfo(testFile.file.name, testFile.content).getOrElse {
                log.error("Test file error: $it")
                it.printStackTrace()
                log.info("Continuing...")
                null
            }
        }.filterNotNull()

        val tasks = mutableListOf<ExecutionTask>()

        tests.forEach { file ->
            file.classContent.forEach { _, suite ->
                suite.testNames.forEach { _, testName ->


                    val task = PairWiseExecutionTask(
                        projectInfo,
                        getCommand(suite.suiteName, testName, projectInfo.config.testCommand),
                        runId
                    )
                    tasks.add(task)
                }
            }
        }

        tasks.forEach { task ->
            workerPool.execute(task)
        }
    }

    fun getCommand(testSuite: String, testName: String, initialCommand: String): String {
        return initialCommand.replace("{def:suite}", testSuite).replace("{def:testName}", testName)
    }
}