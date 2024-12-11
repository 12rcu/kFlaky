package de.matthiasklenz.kflaky

import de.matthiasklenz.kflaky.adapters.commandline.handleCommandLineArgs
import de.matthiasklenz.kflaky.adapters.mapper.map
import de.matthiasklenz.kflaky.adapters.project.ProjectConfigDto
import de.matthiasklenz.kflaky.adapters.terminal.createTerminal
import de.matthiasklenz.kflaky.core.execution.KFlakyTestExecutor
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.nio.file.Paths

fun main(args: Array<String>) = runBlocking {
    val config = handleCommandLineArgs(args)
    val projects = Paths.get("configs").toFile().walk().filter { it.name.endsWith(".json") }.map {
        Json.decodeFromString<ProjectConfigDto>(it.readText()).map()
    }.toList()

    val job = launch {
        KFlakyTestExecutor(projects[0]).runProject()
    }

    job.join()
    return@runBlocking
    val terminal = async {
        createTerminal()
    }

    terminal.await()    //wait for UI exit
    return@runBlocking
}
