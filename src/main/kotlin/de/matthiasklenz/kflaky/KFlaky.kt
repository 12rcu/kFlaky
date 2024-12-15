package de.matthiasklenz.kflaky

import de.matthiasklenz.kflaky.adapters.commandline.handleCommandLineArgs
import de.matthiasklenz.kflaky.adapters.mapper.map
import de.matthiasklenz.kflaky.adapters.project.ProjectConfigDto
import de.matthiasklenz.kflaky.adapters.terminal.createTerminal
import de.matthiasklenz.kflaky.core.execution.KFlakyTestExecutor
import de.matthiasklenz.kflaky.core.project.ProjectProgress
import de.matthiasklenz.kflaky.core.project.ProjectState
import de.matthiasklenz.kflaky.core.project.TestProgress
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import java.nio.file.Paths
import java.util.concurrent.ConcurrentLinkedDeque

fun main(args: Array<String>) = runBlocking {
    val config = handleCommandLineArgs(args)
    val projects = Paths.get("configs").toFile().walk().filter { it.name.endsWith(".json") }.map {
        Json.decodeFromString<ProjectConfigDto>(it.readText()).map()
    }.toList()

    val logChannel = Channel<String>()
    val progressChannel = Channel<List<ProjectProgress>>()

    startKoin {
        modules(
            module {
                single(qualifier = qualifier("log")) { logChannel }
                single(qualifier = qualifier("progress")) { progressChannel }
            }
        )
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

        KFlakyTestExecutor(projects[1], testProgress).also {
            it.runProject()
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
