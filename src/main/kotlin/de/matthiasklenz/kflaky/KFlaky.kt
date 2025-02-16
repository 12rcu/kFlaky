package de.matthiasklenz.kflaky

import de.matthiasklenz.kflaky.core.middleware.configureDj
import de.matthiasklenz.kflaky.core.project.ProjectInfo
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
        single { executionService }
        single { projectService }
    }

    configureDj(initModule)
    val pInfos: MutableList<ProjectInfo> = mutableListOf()
    pInfos.addAll(projects.map { projectService.loadProject(it) })
    if(config.githubQuery != null)
        pInfos.addAll(projectService.searchAndLoadProject(config.githubQuery))
    executionService.executeNewRun(pInfos).join()

    return@runBlocking
}
