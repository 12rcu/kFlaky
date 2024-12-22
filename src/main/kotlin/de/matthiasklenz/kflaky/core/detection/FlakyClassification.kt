package de.matthiasklenz.kflaky.core.detection

import de.matthiasklenz.kflaky.adapters.persistence.SqlLiteDB
import de.matthiasklenz.kflaky.adapters.persistence.tables.DBTestResultsTable
import de.matthiasklenz.kflaky.core.project.ProjectConfig
import de.matthiasklenz.kflaky.core.project.ProjectProgress
import de.matthiasklenz.kflaky.core.project.ProjectState
import de.matthiasklenz.kflaky.core.tasks.TestResultData
import kotlinx.coroutines.channels.Channel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.qualifier
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.filter
import org.ktorm.entity.map
import org.ktorm.entity.sequenceOf
import java.util.concurrent.atomic.AtomicInteger

enum class FlakyClassification {
    NON_FLAKY,
    OD_FLAKY,
    OTHER_FLAKY
}

data class FlakyClassificationData(
    val testSuite: String,
    val testName: String,
    val classification: FlakyClassification
)

class KFlakyClassifier(
    private val projectConfig: ProjectConfig,
    private val projectProgress: ProjectProgress,
    private val runId: Int,
) : KoinComponent {
    private val progressChannel: Channel<ProjectProgress> by inject(qualifier("progress"))
    private val sqlLiteDB: SqlLiteDB by inject()

    suspend fun classify() {
        val testResults = sqlLiteDB.getTestResults(runId, projectConfig.identifier)
        val detection = FlakyDetection().flakyDetection(testResults)

        projectProgress.state = ProjectState.CLASSIFICATION
        projectProgress.index.set(0)
        projectProgress.testsToRun = detection.size
        progressChannel.send(projectProgress)

        detection.forEach { (id, classification) ->
            sqlLiteDB.addClassification(
                runId,
                projectConfig.identifier,
                suite = id.first,
                testId = id.second,
                classification
            )
            projectProgress.index.addAndGet(1)
            progressChannel.send(projectProgress)
        }
    }
}