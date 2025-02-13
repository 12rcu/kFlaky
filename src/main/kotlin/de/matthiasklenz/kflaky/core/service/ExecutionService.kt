package de.matthiasklenz.kflaky.core.service

import de.matthiasklenz.kflaky.KFlakyConfig
import de.matthiasklenz.kflaky.adapters.persistence.SqlLiteDB
import de.matthiasklenz.kflaky.core.execution.WorkerPool
import de.matthiasklenz.kflaky.core.project.ProjectInfo
import de.matthiasklenz.kflaky.core.project.ProjectState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ExecutionService: KoinComponent {
    private val sqlLiteDB: SqlLiteDB by inject()
    private val config: KFlakyConfig by inject()

    private val preRunScheduler: PreRunSchedulerService by inject()
    private val odRunScheduler: OdSchedulerService by inject()
    private val classificationService: ClassificationService by inject()

    suspend fun executeNewRun(projects: List<ProjectInfo>) = coroutineScope {
        val runId = sqlLiteDB.addRun(projects.map { it.config.identifier })

        projects.forEach {
            it.progress.state = ProjectState.NOT_STARTED
        }

        launch {
            projects.forEach {
                val workerPool = WorkerPool(config.worker)
                launch { workerPool.start() }

                preRunScheduler.schedulePreRuns(it, runId, workerPool)
                odRunScheduler.scheduleOdRuns(it, runId, workerPool)

                workerPool.close()
                workerPool.join()

                classificationService.classify(runId, it)
                it.progress.state = ProjectState.DONE
            }
        }
    }
}