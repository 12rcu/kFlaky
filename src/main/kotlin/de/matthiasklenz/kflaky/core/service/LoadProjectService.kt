package de.matthiasklenz.kflaky.core.service

import de.matthiasklenz.kflaky.core.project.ProjectConfig
import de.matthiasklenz.kflaky.core.project.ProjectInfo
import de.matthiasklenz.kflaky.core.project.ProjectProgress
import de.matthiasklenz.kflaky.core.project.ProjectState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.qualifier
import java.util.concurrent.atomic.AtomicInteger

class LoadProjectService : KoinComponent {
    private val projects: HashMap<String, ProjectInfo> by inject(qualifier("projects"))

    fun loadProject(projectConfig: ProjectConfig): ProjectInfo {
        if(projects.contains(projectConfig.identifier)) {
            return projects[projectConfig.identifier]!!
        }

        val progress = ProjectProgress(
            projectConfig.identifier,
            ProjectState.SETUP,
            testsToRun = 0,
            index = AtomicInteger(0),
        )
        val info = ProjectInfo(projectConfig, progress)
        projects[projectConfig.identifier] = info
        return info
    }
}