package de.matthiasklenz.kflaky

import de.matthiasklenz.kflaky.adapters.persistence.KFlakyLogger
import de.matthiasklenz.kflaky.adapters.terminal.createTerminal
import de.matthiasklenz.kflaky.core.configureDj
import de.matthiasklenz.kflaky.core.run.KFlakyRun
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.io.path.Path

fun main() = runBlocking {
    val config = loadConfig(Path("config.json"))
    val projects = config.projects.filter { it.enabled }

    configureDj(config, projects)
    val kFlakyRun = KFlakyRun()

    val runId = kFlakyRun.createRunEntry().await()
    val logger = KFlakyLogger(config, runId)

    launch {
        logger.startWriting()
    }

    kFlakyRun.configureInitialProjectProgress(this)
    val job = kFlakyRun.runFlakyDetection(logger, runId, this)
    val terminal = async {
        createTerminal()
    }

    job.join()
    terminal.await()    //wait for UI exit

    return@runBlocking
}
