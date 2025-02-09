package de.matthiasklenz.kflaky.core.execution

import de.matthiasklenz.kflaky.KFlakyConfig
import de.matthiasklenz.kflaky.adapters.persistence.SqlLiteDB
import de.matthiasklenz.kflaky.core.pool.ExecutionTask
import de.matthiasklenz.kflaky.core.project.ProjectConfig
import de.matthiasklenz.kflaky.core.project.ProjectProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.qualifier
import java.io.File
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission
import kotlin.io.path.absolutePathString
import kotlin.io.path.setPosixFilePermissions

/**
 * @param projectIdentifier the id for the project that is used in the db
 * @param projectProgress the inital progress status of the project that is used to update the command line
 * @param runId the runId referenced in the db
 * @param modifyTestFiles a block that is executed to overwrite test files, as parameter, the worker directory for the project is given
 */
class KFlakyExecutionRecipeTask(
    private val projectIdentifier: String,
    private val projectProgress: ProjectProgress,
    private val runId: Int,
    private val modifyTestFiles: (rootDir: Path) -> Unit,
    private val getTestOrderOf: (testSuiteId: String, testId: String) -> List<Int>,
) : ExecutionTask, KoinComponent {
    private val sqlLiteDB: SqlLiteDB by inject()
    private val config: KFlakyConfig by inject()
    private val progressChannel: Channel<ProjectProgress> by inject(qualifier("progress"))

    private val testCommand = OsCommand()

    private val projectState = projectProgress.copy().state //save imutable state when class was initialized

    override suspend fun execute(worker: Int) = coroutineScope {
        val conf = getProjectConfig()
        cleanAndCopyProjectToWorkingDir(worker)
        modifyTestFiles(getWorkerDir(worker))  //to overwrite the tests, the project root dir is given of the worker
        withContext(Dispatchers.IO) {
            runCommand(worker, conf)
        }
        eval(worker, conf)
        projectProgress.index.set(projectProgress.index.get() + 1)
        progressChannel.send(projectProgress)
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
        projectConfig
            .testResultCollector
            .collect(getTestResultsPath(worker, projectConfig))
            .forEach { result ->
                sqlLiteDB.addTestResult(
                    runId,
                    result,
                    projectConfig.identifier,
                    getTestOrderOf(result.testSuite, result.testName),
                    state = projectState
                )
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
        return config.projects.first { it.identifier == projectIdentifier }
    }
}