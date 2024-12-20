package de.matthiasklenz.kflaky

import de.matthiasklenz.kflaky.adapters.commandline.handleCommandLineArgs
import de.matthiasklenz.kflaky.adapters.mapper.map
import de.matthiasklenz.kflaky.adapters.persistence.KFlakyLogger
import de.matthiasklenz.kflaky.adapters.persistence.SqlLiteDB
import de.matthiasklenz.kflaky.adapters.project.ProjectConfigDto
import de.matthiasklenz.kflaky.adapters.terminal.createTerminal
import de.matthiasklenz.kflaky.core.detection.KFlakyClassifier
import de.matthiasklenz.kflaky.core.execution.KFlakyPreRunExecutor
import de.matthiasklenz.kflaky.core.execution.KFlakyTestExecutor
import de.matthiasklenz.kflaky.core.project.ProjectProgress
import de.matthiasklenz.kflaky.core.project.ProjectState
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import java.io.File
import java.nio.file.Paths

fun main(args: Array<String>) = runBlocking {
    val config = handleCommandLineArgs(args)
    val projects = Paths.get("configs").toFile().walk().filter { it.name.endsWith(".json") }.map {
        Json.decodeFromString<ProjectConfigDto>(it.readText()).map()
    }.toList()

    val logChannel = Channel<String>()
    val progressChannel = Channel<List<ProjectProgress>>()
    val db = SqlLiteDB()

    startKoin {
        modules(
            module {
                single(qualifier = qualifier("log")) { logChannel }
                single(qualifier = qualifier("progress")) { progressChannel }
                single { db }
            }
        )
    }

    val runId = db.addRun(projects.map { it.identifier })
    File("logs").mkdir()
    val logger = KFlakyLogger(File("logs/log-$runId.txt"))

    launch {
        logger.startWriting()
    }

    val job = launch {
        val testProgress: List<ProjectProgress> = projects.map {
            ProjectProgress(
                it.identifier,
                ProjectState.NOT_STARTED,
                0,
                0
            )
        }

        progressChannel.send(testProgress)

        projects.forEach {
            logger.setProject(it.identifier)
            KFlakyPreRunExecutor(it, testProgress, runId).executePreRuns()
            //todo use results form pre runs to disable tests!
            KFlakyTestExecutor(it, testProgress, runId).runProject()
            KFlakyClassifier(runId, it, testProgress).classify()

            testProgress.filter { progress -> progress.name == it.identifier }.forEach { progress -> progress.state = ProjectState.DONE }
            progressChannel.send(testProgress)
        }

        logChannel.close()
        progressChannel.close()
    }

    val terminal = async {
        createTerminal()
    }

    job.join()
    terminal.await()    //wait for UI exit

    return@runBlocking
}
