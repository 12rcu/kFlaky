package de.matthiasklenz.kflaky.core.execution

import de.matthiasklenz.kflaky.KFlakyConfig
import de.matthiasklenz.kflaky.adapters.persistence.SqlLiteDB
import de.matthiasklenz.kflaky.core.middleware.KFlakyLogger
import de.matthiasklenz.kflaky.core.project.ProjectConfig
import de.matthiasklenz.kflaky.core.project.ProjectInfo
import de.matthiasklenz.kflaky.core.project.ProjectState
import de.matthiasklenz.kflaky.core.tasks.TestOutcomeInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission
import kotlin.io.path.setPosixFilePermissions

/**
 * @param runId the runId referenced in the db
 * @param modifyTestFiles a block that is executed to overwrite test files, as parameter, the worker directory for the project is given
 */
class KFlakyExecutionRecipeTask(
    private val projectInfo: ProjectInfo,
    private val runId: Int,
    private val iteration: Int,
    private val runType: RunType,
    private val modifyTestFiles: (rootDir: Path) -> Unit,
    private val getTestOrderOf: (testSuiteId: String) -> List<Int>,
) : ExecutionTask, KoinComponent {
    private val sqlLiteDB: SqlLiteDB by inject()
    private val config: KFlakyConfig by inject()
    private val testCommand = OsCommand()
    private val logger: KFlakyLogger by inject()

    override suspend fun execute(worker: Int) = coroutineScope {
        val conf = getProjectConfig()
        cleanAndCopyProjectToWorkingDir(worker)
        modifyTestFiles(getWorkerDir(worker))  //to overwrite the tests, the project root dir is given of the worker
        withContext(Dispatchers.IO) {
            runCommand(worker, conf)
        }
        eval(worker, conf)
        projectInfo.progress.state = if (runType == RunType.OD) ProjectState.OD_RUNS else ProjectState.PRE_RUNS
        projectInfo.progress.index.addAndGet(1)
        return@coroutineScope
    }

    private fun cleanAndCopyProjectToWorkingDir(worker: Int) {
        val workerPath = getWorkerDir(worker)
        workerPath.toFile().deleteRecursively()
        workerPath.toFile().mkdirs()
        getProjectConfig().projectPath.toFile().copyRecursively(workerPath.toFile())
    }

    private suspend fun runCommand(worker: Int, projectConfig: ProjectConfig) {
        val execPath = getTestExecPath(worker, projectConfig)
        val execFile = execPath.resolve(projectConfig.testCommand.split(" ").firstOrNull() ?: "")
        val isWindows = System.getProperty("os.name").lowercase().startsWith("windows");

        if (!isWindows) {
            execFile.setPosixFilePermissions(
                setOf(
                    PosixFilePermission.GROUP_EXECUTE,
                    PosixFilePermission.GROUP_READ,
                    PosixFilePermission.GROUP_WRITE,
                    PosixFilePermission.OTHERS_EXECUTE,
                    PosixFilePermission.OTHERS_READ,
                    PosixFilePermission.OTHERS_WRITE,
                    PosixFilePermission.OWNER_EXECUTE,
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE
                )
            )
        }
        testCommand.executeTestCommand(projectConfig.testCommand, execPath.toFile(), "worker-$worker")
    }

    private suspend fun eval(worker: Int, projectConfig: ProjectConfig) {
        val log = logger.get("ExecTask")

        val testSuitResults = projectConfig
            .testResultCollector
            .collect(getTestResultsPath(worker, projectConfig), getTestOrderOf)

        val ops = sqlLiteDB.addTestResult(
            TestOutcomeInfo(runId, iteration, runType, testSuitResults),
            projectInfo.config.identifier
        )
        if (ops == 0) {
            log.warn("No test results found for id $runId in iteration $iteration of project ${projectConfig.identifier}")
        }
    }

    //=============== Utility functions ===============

    private fun getTestExecPath(worker: Int, projectConfig: ProjectConfig): Path {
        return if (projectConfig.testExecutionPath.isNotEmpty()) {
            getWorkerDir(worker).resolve(projectConfig.testExecutionPath)
        } else {
            getWorkerDir(worker)
        }
    }

    private fun getTestResultsPath(worker: Int, projectConfig: ProjectConfig): Path {
        return if (projectConfig.testResultDir.isNotEmpty()) {
            getWorkerDir(worker).resolve(projectConfig.testResultDir)
        } else {
            getWorkerDir(worker)
        }
    }

    private fun getTestPath(worker: Int, projectConfig: ProjectConfig): Path {
        return if (projectConfig.testSrcDir.isNotEmpty()) {
            getWorkerDir(worker).resolve(projectConfig.testSrcDir)
        } else {
            getWorkerDir(worker)
        }
    }

    private fun getWorkerDir(worker: Int): Path {
        return config.tmpDir.resolve("workers").resolve("worker-$worker")
    }

    private fun getProjectConfig(): ProjectConfig {
        //we assert that the project exists
        return config.projects.first { it.identifier == projectInfo.config.identifier }
    }
}