package de.matthiasklenz.kflaky

import de.matthiasklenz.kflaky.adapters.mapper.map
import de.matthiasklenz.kflaky.adapters.persistence.KFlakyLogger
import de.matthiasklenz.kflaky.adapters.persistence.SqlLiteDB
import de.matthiasklenz.kflaky.adapters.terminal.createTerminal
import de.matthiasklenz.kflaky.core.detection.KFlakyClassifier
import de.matthiasklenz.kflaky.core.execution.KFlakyOdTestExecutor
import de.matthiasklenz.kflaky.core.execution.KFlakyPreRunExecutor
import de.matthiasklenz.kflaky.core.pool.WorkerPool
import de.matthiasklenz.kflaky.core.project.ProjectProgress
import de.matthiasklenz.kflaky.core.project.ProjectState
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import java.util.concurrent.atomic.AtomicInteger
import kotlin.io.path.Path

fun main() = runBlocking {
    val config = Path("config.json").toFile().readText()
    val kFlakyConfig = Json.decodeFromString<KFlakyConfigDto>(config).map()
    val projects = kFlakyConfig.projects

    val logChannel = Channel<String>()
    val terminalLogChannel = Channel<String>()
    val progressChannel = Channel<ProjectProgress>()
    val db = SqlLiteDB()
    val workerPool = WorkerPool(kFlakyConfig.worker)

    startKoin {
        modules(
            module {
                single(qualifier = qualifier("log")) { logChannel }
                single(qualifier = qualifier("terminal")) { terminalLogChannel }
                single(qualifier = qualifier("progress")) { progressChannel }
                single { db }
                single { workerPool }
                single { kFlakyConfig }
            }
        )
    }

    val runId = db.addRun(projects.map { it.identifier })
    val logger = KFlakyLogger(kFlakyConfig, runId)

    launch {
        logger.startWriting()
    }

    val progressList = mutableListOf<ProjectProgress>()
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

    val job = launch {
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
        terminalLogChannel.close()
    }

    val terminal = async {
        createTerminal()
    }

    job.join()
    terminal.await()    //wait for UI exit

    return@runBlocking
}
