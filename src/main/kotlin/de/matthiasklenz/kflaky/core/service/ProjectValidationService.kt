package de.matthiasklenz.kflaky.core.service

import de.matthiasklenz.kflaky.core.execution.KFlakyExecutionRecipeTask
import de.matthiasklenz.kflaky.core.execution.RunType
import de.matthiasklenz.kflaky.core.middleware.KFlakyLogger
import de.matthiasklenz.kflaky.core.project.ProjectInfo
import de.matthiasklenz.kflaky.core.project.ProjectState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProjectValidationService : KoinComponent {
    private val logger: KFlakyLogger by inject()

    suspend fun validate(runId: Int, projectInfo: ProjectInfo): Boolean {
        val log = logger.get("ProjectValidation")
        log.info("Start validation of project ${projectInfo.config.identifier}")
        projectInfo.progress.state = ProjectState.VALIDATING
        projectInfo.progress.testsToRun = 1
        projectInfo.progress.index.set(0)
        val task = KFlakyExecutionRecipeTask(
            projectInfo,
            runId,
            -1,
            RunType.PRE_RUN,
            {}
        ) { _ -> listOf() }

        val results = try {
             task.execute(0)
        } catch (e: Exception) {
            log.warn("Error while project validation: ${e.message}")
            e.printStackTrace()
            0
        }

        log.info("Finish validation of project ${projectInfo.config.identifier}, found $results test results")
        return results > 0
    }
}