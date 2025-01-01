package de.matthiasklenz.kflaky.core.run

import de.matthiasklenz.kflaky.adapters.persistence.KFlakyLogger
import de.matthiasklenz.kflaky.adapters.persistence.SqlLiteDB
import de.matthiasklenz.kflaky.core.detection.KFlakyClassifier
import de.matthiasklenz.kflaky.core.execution.KFlakyOdTestExecutor
import de.matthiasklenz.kflaky.core.execution.KFlakyPreRunExecutor
import de.matthiasklenz.kflaky.core.pool.WorkerPool
import de.matthiasklenz.kflaky.core.project.ProjectConfig
import de.matthiasklenz.kflaky.core.project.ProjectProgress
import de.matthiasklenz.kflaky.core.project.ProjectState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.qualifier
import java.util.concurrent.atomic.AtomicInteger
import kotlin.getValue

class KFlakyRun: KoinComponent {
    private val sqlLiteDB: SqlLiteDB by inject()
    private val projects: List<ProjectConfig> by inject()
    private val terminalLog: Channel<String> by inject(qualifier("terminal"))
    private val logChannel: Channel<String> by inject(qualifier("log"))
    private val progressChannel: Channel<ProjectProgress> by inject(qualifier("progress"))
    private val workerPool: WorkerPool by inject()
    private val progressList = mutableListOf<ProjectProgress>()

    suspend fun createRunEntry(): Deferred<Int> = coroutineScope {
        async {
            sqlLiteDB.addRun(projects.map { it.identifier })
        }
    }

    fun configureInitialProjectProgress(scope: CoroutineScope) = with(scope) {
        projects.forEach {
            val progress =  ProjectProgress(
                it.identifier,
                ProjectState.NOT_STARTED,
                0,
                AtomicInteger(0)
            )
            progressList.add(progress)
            launch {
                progressChannel.send(
                    progress
                )
            }
        }
    }

    fun runFlakyDetection(logger: KFlakyLogger, runId: Int, scope: CoroutineScope): Job = with(scope) {
        return launch {
            projects.forEach {
                launch {
                    workerPool.start()
                }

                logger.setProject(it.identifier)
                val progress = progressList.first { p -> p.name == it.identifier }
                val preRun = KFlakyPreRunExecutor(it, progress, runId)
                val odRun = KFlakyOdTestExecutor(it, progress, runId)

                progress.testsToRun = it.preRuns + odRun.testRuns

                preRun.executePreRuns()
                odRun.runProject()

                workerPool.close()
                workerPool.join()   //await all test results

                val classifier = KFlakyClassifier(it, progress, runId)
                classifier.classify()

                progress.state = ProjectState.DONE
                progressChannel.send(progress)
            }
            logChannel.close()
            progressChannel.close()
            terminalLog.close()
        }
    }
}