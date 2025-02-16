package de.matthiasklenz.kflaky.core.service

import de.matthiasklenz.kflaky.KFlakyConfig
import de.matthiasklenz.kflaky.adapters.github.GithubRequest
import de.matthiasklenz.kflaky.adapters.mapper.map
import de.matthiasklenz.kflaky.core.middleware.KFlakyLogger
import de.matthiasklenz.kflaky.core.project.ProjectConfig
import de.matthiasklenz.kflaky.core.project.ProjectInfo
import de.matthiasklenz.kflaky.core.project.ProjectProgress
import de.matthiasklenz.kflaky.core.project.ProjectState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.qualifier
import java.util.concurrent.atomic.AtomicInteger
import kotlin.io.path.exists

class LoadProjectService : KoinComponent {
    private val projects: HashMap<String, ProjectInfo> by inject(qualifier("projects"))
    private val gitService: GitService by inject()
    private val config: KFlakyConfig by inject()
    private val logger: KFlakyLogger by inject()
    private val dynamicProjectService: DynamicProjectService by inject()

    suspend fun loadProject(projectConfig: ProjectConfig): ProjectInfo {
        val log = logger.get("ProjectInit")

        if (projects.contains(projectConfig.identifier)) {
            log.warn("Project ${projectConfig.identifier} already exists, loading existing project!")
            return projects[projectConfig.identifier]!!
        }

        if (projectConfig.projectUri.endsWith(".git") && !projectConfig.projectPath.exists()) {
            log.info("Download project ${projectConfig.identifier} from ${projectConfig.projectUri}")
            val targetDir = config.tmpDir.resolve("projects").resolve(projectConfig.identifier).toFile()
            val status = gitService.download(projectConfig.projectUri, targetDir)
            if (status != 0) {
                log.error("Failed to download project ${projectConfig.identifier}, details in log file!")
                error("Failed to download project ${projectConfig.identifier}: $status")
            }
            log.info("Set (${projectConfig.identifier}) project path to ${targetDir.absolutePath}")
            projectConfig.projectPath = targetDir.toPath()
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

    suspend fun searchAndLoadProject(githubRequest: GithubRequest): List<ProjectInfo> {
        val log = logger.get("GithubSearchService")

        val response = gitService.searchRepositories(githubRequest)
        if (response == null) {
            log.warn("Something went wrong while executing github search!")
            return emptyList()
        }
        if (response.items.isEmpty()) {
            log.warn("GitHub search found no project!")
        }
        return response.items.mapNotNull {
            val targetDir = config
                .tmpDir
                .resolve("projects")
                .resolve("${it.name}_${it.id.toString().take(3)}")
                .toFile()
            val status = gitService.download(it.cloneUrl, targetDir)
            if (status != 0) {
                log.error("Failed to download project ${it.name}, details in log file!")
                log.debug("Project info dump: ${Json.encodeToString(it)}")
                return@mapNotNull null
            }
            val p = dynamicProjectService.detectConfig(targetDir, it)?.map()
            if (p != null) {
                loadProject(p)
            } else {
                log.warn("No config detected for ${it.name}, details in log file!")
                log.debug("No config detected for ${it.name}: ${Json.encodeToString(it)}")
                null
            }
        }
    }
}