package de.matthiasklenz.kflaky.core.execution

import de.matthiasklenz.kflaky.KFlakyConfig
import de.matthiasklenz.kflaky.adapters.persistence.SqlLiteDB
import de.matthiasklenz.kflaky.core.middleware.KFlakyLogger
import de.matthiasklenz.kflaky.core.project.ProjectConfig
import de.matthiasklenz.kflaky.core.project.ProjectInfo
import de.matthiasklenz.kflaky.core.project.ProjectState
import de.matthiasklenz.kflaky.core.tasks.TestOutcomeInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.nio.file.Path
import kotlin.getValue

class PairWiseExecutionTask(
    private val projectInfo: ProjectInfo,
    private val command: String,
    private val runId: Int,
) : ExecutionTask, KoinComponent {
    private val config: KFlakyConfig by inject()
    private val testCommand = OsCommand()
    private val logger: KFlakyLogger by inject()
    private val sqlLiteDB: SqlLiteDB by inject()

    override suspend fun execute(worker: Int): Int {
        cleanAndCopyProjectToWorkingDir(worker)
        withContext(Dispatchers.IO) {
            runCommand(worker, projectInfo.config)
        }
        val results = eval(worker, projectInfo.config)
        projectInfo.progress.state = ProjectState.OD_RUNS
        projectInfo.progress.index.addAndGet(1)
        return results
    }

    private suspend fun eval(worker: Int, projectConfig: ProjectConfig): Int {
        val log = logger.get("PairWiseExec")

        val testSuitResults = projectConfig
            .testResultCollector
            .collect(getTestResultsPath(worker, projectConfig)) { listOf() }

        val ops = sqlLiteDB.addTestResult(
            TestOutcomeInfo(runId, 0, RunType.OD, testSuitResults),
            projectInfo.config.identifier
        )
        if (ops == 0) {
            log.warn("No test results found for id $runId of project ${projectConfig.identifier}")
        }

        return ops
    }


    private fun getTestExecPath(worker: Int, projectConfig: ProjectConfig): Path {
        return if (projectConfig.testExecutionPath.isNotEmpty()) {
            getWorkerDir(worker).resolve(projectConfig.testExecutionPath)
        } else {
            getWorkerDir(worker)
        }
    }

    private suspend fun runCommand(worker: Int, projectConfig: ProjectConfig) {
        val execPath = getTestExecPath(worker, projectConfig)
        testCommand.setExecPerms(command, execPath.toFile())
        testCommand.executeTestCommand(command, execPath.toFile(), "worker-$worker")
    }

    private fun getTestResultsPath(worker: Int, projectConfig: ProjectConfig): Path {
        return if (projectConfig.testResultDir.isNotEmpty()) {
            getWorkerDir(worker).resolve(projectConfig.testResultDir)
        } else {
            getWorkerDir(worker)
        }
    }

    private fun cleanAndCopyProjectToWorkingDir(worker: Int) {
        val workerPath = getWorkerDir(worker)
        workerPath.toFile().deleteRecursively()
        workerPath.toFile().mkdirs()
        projectInfo.config.projectPath.toFile().copyRecursively(workerPath.toFile())
    }

    private fun getWorkerDir(worker: Int): Path {
        return config.tmpDir.resolve("workers").resolve("worker-$worker")
    }
}