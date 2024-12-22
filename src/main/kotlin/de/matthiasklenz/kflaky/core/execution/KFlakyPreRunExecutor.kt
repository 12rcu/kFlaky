package de.matthiasklenz.kflaky.core.execution

import de.matthiasklenz.kflaky.core.pool.WorkerPool
import de.matthiasklenz.kflaky.core.project.ProjectConfig
import de.matthiasklenz.kflaky.core.project.ProjectProgress
import de.matthiasklenz.kflaky.core.project.ProjectState
import kotlinx.coroutines.channels.Channel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.qualifier

class KFlakyPreRunExecutor(
    private val projectConfig: ProjectConfig,
    private val projectProgress: ProjectProgress,
    private val runId: Int
) : KoinComponent {
    private val workerPool: WorkerPool by inject()
    private val terminalLog: Channel<String> by inject(qualifier("terminal"))
    private val progressChannel: Channel<ProjectProgress> by inject(qualifier("progress"))

    suspend fun executePreRuns() {
        projectProgress.state = ProjectState.PRE_RUNS
        progressChannel.send(projectProgress)

        for (index in 0 until projectConfig.preRuns) {
            terminalLog.send("[${projectConfig.identifier}] Seceduled pre test execution ${index + 1}/$${projectConfig.preRuns}")
            val task = KFlakyExecutionRecipeTask(
                projectConfig.identifier,
                projectProgress,
                runId,
                {}
            ) { _, _ -> listOf() }
            workerPool.execute(task)
        }
    }
}

