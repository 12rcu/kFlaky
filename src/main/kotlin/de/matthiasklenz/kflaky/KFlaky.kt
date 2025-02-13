package de.matthiasklenz.kflaky

import de.matthiasklenz.kflaky.core.middleware.configureDj
import de.matthiasklenz.kflaky.core.service.ExecutionService
import de.matthiasklenz.kflaky.core.service.LoadProjectService
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module
import kotlin.io.path.Path

fun main() = runBlocking {
    val config = loadConfig(Path("config.json"))
    val projects = config.projects.filter { it.enabled }

    val projectService = LoadProjectService()
    val executionService = ExecutionService()

    val initModule = module {
        single { config }
        single { projects }

        single { executionService }
        single { projectService }
    }

    configureDj(initModule)
    val pInfos = projects.map { projectService.loadProject(it) }
    executionService.executeNewRun(pInfos).join()

    return@runBlocking
}
