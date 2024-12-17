package de.matthiasklenz.kflaky.core.detection

import de.matthiasklenz.kflaky.adapters.persistence.tables.DBTestResultsEntity
import de.matthiasklenz.kflaky.adapters.persistence.tables.DBTestResultsTable
import de.matthiasklenz.kflaky.core.project.ProjectState
import kotlinx.coroutines.channels.Channel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.qualifier
import org.ktorm.entity.EntitySequence
import org.ktorm.entity.toList

class FlakyDetection: KoinComponent {
    private val logChannel: Channel<String> by inject(qualifier("log"))

    /**
     * @return a map of testSuite to testName pairs, and the classification
     */
    suspend fun flakyDetection(testData: EntitySequence<DBTestResultsEntity, DBTestResultsTable>): Map<Pair<String, String>, FlakyClassification> {
        val tests = testData.toList().groupBy { it.testSuite to it.testId }
        return tests.map { (id, idTests) ->
            val preRunResults = idTests.filter { it.runType == ProjectState.PRE_RUNS }.map { it.result }
            val odRunResults = idTests.filter { it.runType == ProjectState.OD_RUNS }.map { it.result }
            var classification = FlakyClassification.NON_FLAKY
            if (odRunResults.distinct().size > 1) {
                classification = FlakyClassification.OD_FLAKY
            }
            if (preRunResults.distinct().size > 1) {
                classification = FlakyClassification.OTHER_FLAKY
            }
            logChannel.send("[${id.first}|${id.second}] is: $classification")
            id to classification
        }.toMap()
    }
}

