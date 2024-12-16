package de.matthiasklenz.kflaky.core.execution

import de.matthiasklenz.kflaky.adapters.persistence.SqlLiteDB
import de.matthiasklenz.kflaky.core.project.ProjectConfig
import de.matthiasklenz.kflaky.core.project.ProjectProgress
import de.matthiasklenz.kflaky.core.project.ProjectState
import kotlinx.coroutines.channels.Channel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.qualifier

class KFlakyPreRunExecutor(
    private val projectConfig: ProjectConfig,
    private val projectProgress: List<ProjectProgress>,
    private val runId: Int
) : KoinComponent {
    private val sqlLiteDB: SqlLiteDB by inject()
    private val progressChannel: Channel<List<ProjectProgress>> by inject(qualifier("progress"))
    private val testCommand = OsCommand()
    private val progress = projectProgress.filter { it.name == projectConfig.identifier }

    suspend fun executePreRuns() {
        progress.forEach {
            it.state = ProjectState.PRE_RUNS
            it.testsToRun = projectConfig.preRuns
            it.index = 0
        }
        progressChannel.send(projectProgress)
        for (i in 0 until projectConfig.preRuns) {
            progress.forEach { it.index = i }
            progressChannel.send(projectProgress)
            runCommand(projectConfig)
            eval(projectConfig)
        }
        //reset for OD detection
        progress.forEach {
            it.testsToRun = 0
            it.index = 0
        }
    }

    private suspend fun runCommand(projectConfig: ProjectConfig) {
        testCommand.executeTestCommand(projectConfig.testCommand, projectConfig.testExecutionPath.toFile())
    }

    private fun eval(projectConfig: ProjectConfig) {
        projectConfig.testResultCollector.collect(projectConfig.testResultDir).forEach { result ->
            sqlLiteDB.addTestResult(
                runId,
                result,
                projectConfig.identifier,
                listOf(),
                state = ProjectState.PRE_RUNS
            )
        }
    }
}

