package de.matthiasklenz.kflaky.core.service

import de.matthiasklenz.kflaky.adapters.persistence.SqlLiteDB
import de.matthiasklenz.kflaky.core.execution.OsCommand
import de.matthiasklenz.kflaky.core.execution.RunType
import de.matthiasklenz.kflaky.core.middleware.KFlakyLogger
import de.matthiasklenz.kflaky.core.project.ProjectInfo
import de.matthiasklenz.kflaky.core.tasks.TestOutcomeInfo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProjectValidationService : KoinComponent {
    private val sqlLiteDB: SqlLiteDB by inject()
    private val logger: KFlakyLogger by inject()

    suspend fun validate(runId: Int, projectInfo: ProjectInfo): Boolean {
        val log = logger.get("ProjectValidation")

        OsCommand().executeTestCommand(
            projectInfo.config.testCommand,
            projectInfo.config.projectPath.toFile(),
            "ValidationRunner"
        )

        val resultDir = if(projectInfo.config.testResultDir != "") {
            projectInfo.config.projectPath.resolve(projectInfo.config.testResultDir)
        } else {
            projectInfo.config.projectPath
        }

        val testSuitResults = projectInfo.config
            .testResultCollector
            .collect(resultDir) { listOf() }

        if(testSuitResults.isEmpty()) {
            log.error("Could not find test results for ${projectInfo.config.identifier}!")
            return false
        }

        sqlLiteDB.addTestResult(
            TestOutcomeInfo(runId, -1, RunType.PRE_RUN, testSuitResults),
            projectInfo.config.identifier
        )

        return true
    }
}