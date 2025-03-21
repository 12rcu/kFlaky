package de.matthiasklenz.kflaky.core.service

import de.matthiasklenz.kflaky.KFlakyConfig
import de.matthiasklenz.kflaky.adapters.persistence.SqlLiteDB
import de.matthiasklenz.kflaky.core.execution.WorkerPool
import de.matthiasklenz.kflaky.core.middleware.KFlakyLogger
import de.matthiasklenz.kflaky.core.project.ProjectInfo
import de.matthiasklenz.kflaky.core.project.ProjectState
import de.matthiasklenz.kflaky.core.strategy.TestExecutionStrategy
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ExecutionService : KoinComponent {
    private val sqlLiteDB: SqlLiteDB by inject()
    private val config: KFlakyConfig by inject()
    private val logger: KFlakyLogger by inject()

    private val projectValidationService: ProjectValidationService by inject()

    private val preRunScheduler: PreRunSchedulerService by inject()
    private val odRunScheduler: OdSchedulerService by inject()
    private val pairWiseScheduler: PairWiseSchedulerService by inject()
    private val classificationService: ClassificationService by inject()

    suspend fun executeNewRun(projects: List<ProjectInfo>) = coroutineScope {
        val log = logger.get("ExecutionService")
        val runId = sqlLiteDB.addRun(projects.map { it.config.identifier })

        projects.forEach {
            it.progress.state = ProjectState.VALIDATING
            if (it.config.strategy != TestExecutionStrategy.PAIR_WISE && !projectValidationService.validate(runId, it)) {
                log.error("Could not validate project: ${it.config.identifier}!")
                //error("Could not validate project: ${it.config.identifier}!")
                it.config.enabled = false
            }
        }

        projects.forEach {
            it.progress.state = ProjectState.NOT_STARTED
        }

        launch {
            projects.filter { it.config.enabled }.forEach {
                val workerPool = WorkerPool(config.worker)
                launch { workerPool.start() }

                preRunScheduler.schedulePreRuns(it, runId, workerPool)
                if (it.config.strategy == TestExecutionStrategy.PAIR_WISE) {
                    pairWiseScheduler.schedulePairWiseRuns(it, runId, workerPool)
                } else {
                    odRunScheduler.scheduleOdRuns(it, runId, workerPool)
                }

                workerPool.close()
                workerPool.join()

                classificationService.classify(runId, it)
                it.progress.state = ProjectState.DONE
            }
        }
    }
}