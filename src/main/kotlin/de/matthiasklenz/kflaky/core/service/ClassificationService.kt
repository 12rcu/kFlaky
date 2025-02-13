package de.matthiasklenz.kflaky.core.service

import de.matthiasklenz.kflaky.adapters.persistence.SqlLiteDB
import de.matthiasklenz.kflaky.core.project.ProjectInfo
import de.matthiasklenz.kflaky.core.project.ProjectState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ClassificationService : KoinComponent {
    private val sqlLiteDB: SqlLiteDB by inject()
    private val flakyDetectionService: FlakyDetectionService by inject()

    suspend fun classify(runId: Int, project: ProjectInfo) {
        project.progress.state = ProjectState.CLASSIFICATION
        project.progress.index.set(0)
        project.progress.testsToRun = 2

        val testResults = sqlLiteDB.getTestResults(runId, project.config.identifier)
        val flakyResults = flakyDetectionService.flakyDetection(testResults)

        project.progress.index.set(1)

        flakyResults.forEach { (id, classification) ->
            sqlLiteDB.addClassification(
                runId,
                project.config.identifier,
                suite = id.first,
                testId = id.second,
                classification
            )
        }

        project.progress.index.set(2)
    }
}