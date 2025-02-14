package de.matthiasklenz.kflaky.adapters.persistence

import de.matthiasklenz.kflaky.adapters.persistence.tables.DBRunsTable
import de.matthiasklenz.kflaky.adapters.persistence.tables.DBTestClassificationsTable
import de.matthiasklenz.kflaky.adapters.persistence.tables.DBTestResultsEntity
import de.matthiasklenz.kflaky.adapters.persistence.tables.DBTestResultsTable
import de.matthiasklenz.kflaky.adapters.persistence.tables.DBTestResultsTable.testOrder
import de.matthiasklenz.kflaky.core.classification.FlakyClassification
import de.matthiasklenz.kflaky.core.project.ProjectState
import de.matthiasklenz.kflaky.core.tasks.TestOutcomeInfo
import de.matthiasklenz.kflaky.core.tasks.TestResultData
import de.matthiasklenz.kflaky.core.tasks.TestSuiteResultInfo
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.*
import org.ktorm.support.sqlite.bulkInsert

class SqlLiteDB {
    private val transactionLock = Semaphore(1)

    val database: Database = Database.connect(
        "jdbc:sqlite:default.db"
    )

    suspend fun addRun(projects: List<String>): Int {
        transactionLock.withPermit {
            database.insert(DBRunsTable) {
                set(it.projects, projects.joinToString(", "))
            }
        }
        return database.sequenceOf(DBRunsTable).last().id
    }

    suspend fun addTestResult(
        data: TestOutcomeInfo,
        project: String,
    ): Int {
        if(data.testSuiteResults.isEmpty()) {
            return 0
        }
        transactionLock.withPermit {
            return database.bulkInsert(DBTestResultsTable) {
                data.testSuiteResults.forEach { suite ->
                    if (suite.order.contains(-1))
                        return@forEach
                    suite.testResults.forEach { test ->
                        item {
                            set(it.project, project)
                            set(it.runId, data.runId)
                            set(it.iteration, data.iteration)
                            set(it.testOrder, suite.order.joinToString(","))
                            set(it.runType, data.runType)
                            set(it.testId, test.testName)
                            set(it.result, test.outcome)
                            set(it.testSuite, suite.name)
                            set(it.raw, suite.raw)
                        }
                    }
                }
            }
        }
    }

    fun getTestNum(runId: Int, projectId: String): Int {
        return database
            .sequenceOf(DBTestResultsTable)
            .filter { (it.runId eq runId) and (it.project eq projectId) }
            .count()
    }

    fun getTestResults(runId: Int, projectId: String): EntitySequence<DBTestResultsEntity, DBTestResultsTable> {
        return database
            .sequenceOf(DBTestResultsTable)
            .filter { (it.runId eq runId) and (it.project eq projectId) }
    }

    suspend fun addClassification(runId: Int, projectId: String, data: Map<Pair<String, String>, FlakyClassification>) {
        transactionLock.withPermit {
            database.bulkInsert(DBTestClassificationsTable) {
                data.forEach { (id, classification) ->
                    item {
                        set(it.runId, runId)
                        set(it.project, projectId)
                        set(it.testSuite, id.first)
                        set(it.testId, id.second)
                        set(it.classification, classification)
                    }
                }
            }
        }
    }

    suspend fun addClassification(
        runId: Int,
        projectId: String,
        suite: String,
        testId: String,
        classification: FlakyClassification
    ) {
        transactionLock.withPermit {
            database.insert(DBTestClassificationsTable) {
                set(it.runId, runId)
                set(it.project, projectId)
                set(it.testSuite, suite)
                set(it.testId, testId)
                set(it.classification, classification)
            }
        }
    }
}