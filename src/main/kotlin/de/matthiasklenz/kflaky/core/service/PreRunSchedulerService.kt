package de.matthiasklenz.kflaky.core.service

import de.matthiasklenz.kflaky.core.execution.KFlakyExecutionRecipeTask
import de.matthiasklenz.kflaky.core.execution.RunType
import de.matthiasklenz.kflaky.core.middleware.KFlakyLogger
import de.matthiasklenz.kflaky.core.execution.WorkerPool
import de.matthiasklenz.kflaky.core.project.ProjectInfo
import de.matthiasklenz.kflaky.core.project.ProjectState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PreRunSchedulerService : KoinComponent {
    private val logger: KFlakyLogger by inject()

    suspend fun schedulePreRuns(projectInfo: ProjectInfo, runId: Int, workerPool: WorkerPool) {
        projectInfo.progress.state = ProjectState.PRE_RUNS

        for (index in 0 until projectInfo.config.preRuns) {
            logger
                .get("PreRun Scheduler")
                .log(
                    "[${projectInfo.config.identifier}] Scheduled pre test execution ${index + 1}/${projectInfo.config.preRuns}"
                )
            val task = KFlakyExecutionRecipeTask(
                projectInfo,
                runId,
                index,
                RunType.PRE_RUN,
                {}
            ) { _ -> listOf() }
            workerPool.execute(task)
        }
    }
}