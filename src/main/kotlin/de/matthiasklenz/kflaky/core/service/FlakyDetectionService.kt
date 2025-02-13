package de.matthiasklenz.kflaky.core.service

import de.matthiasklenz.kflaky.adapters.persistence.tables.DBTestResultsEntity
import de.matthiasklenz.kflaky.adapters.persistence.tables.DBTestResultsTable
import de.matthiasklenz.kflaky.core.classification.FlakyClassification
import de.matthiasklenz.kflaky.core.execution.RunType
import de.matthiasklenz.kflaky.core.middleware.KFlakyLogger
import kotlinx.coroutines.channels.Channel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.qualifier
import org.ktorm.entity.EntitySequence
import org.ktorm.entity.toList

class FlakyDetectionService: KoinComponent {
    private val logger: KFlakyLogger by inject()

    /**
     * @return a map of testSuite to testName pairs, and the classification
     */
    fun flakyDetection(testData: EntitySequence<DBTestResultsEntity, DBTestResultsTable>): Map<Pair<String, String>, FlakyClassification> {
        val tests = testData.toList().groupBy { it.testSuite to it.testId }
        return tests.map { (id, idTests) ->
            val preRunResults = idTests.filter { it.runType == RunType.PRE_RUN }.map { it.result }
            val odRunResults = idTests.filter { it.runType == RunType.OD }.map { it.result }
            var classification = FlakyClassification.NON_FLAKY
            if (odRunResults.distinct().size > 1) {
                classification = FlakyClassification.OD_FLAKY
            }
            if (preRunResults.distinct().size > 1) {
                classification = FlakyClassification.OTHER_FLAKY
            }
            logger.get("FlakyDetectionService").log("[${id.first}|${id.second}] is: $classification")
            id to classification
        }.toMap()
    }
}

