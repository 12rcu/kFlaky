package de.matthiasklenz.kflaky.adapters.persistence

import de.matthiasklenz.kflaky.adapters.persistence.tables.DBRunsTable
import de.matthiasklenz.kflaky.adapters.persistence.tables.DBTestClassificationsTable
import de.matthiasklenz.kflaky.adapters.persistence.tables.DBTestResultsEntity
import de.matthiasklenz.kflaky.adapters.persistence.tables.DBTestResultsTable
import de.matthiasklenz.kflaky.core.detection.FlakyClassification
import de.matthiasklenz.kflaky.core.project.ProjectState
import de.matthiasklenz.kflaky.core.tasks.TestResultData
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.*

class SqlLiteDB {
    val database: Database = Database.connect(
        "jdbc:sqlite:default.db"
    )

    fun addRun(projects: List<String>): Int {
        database.insert(DBRunsTable) {
            set(it.projects, projects.joinToString(", "))
        }

        return database.sequenceOf(DBRunsTable).last().id
    }

    fun addTestResult(run: Int, data: TestResultData, project: String, testOrder: List<Int>, state: ProjectState) {
        database.insert(DBTestResultsTable) {
            set(it.project, project)
            set(it.testId, data.testName)
            set(it.result, data.outcome)
            set(it.runId, run)
            set(it.testOrder, testOrder.joinToString(","))
            set(it.testSuite, data.testSuite)
            set(it.runType, state)
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

    fun addClassification(runId: Int, projectId: String, suite: String, testId: String, classification: FlakyClassification) {
        database.insert(DBTestClassificationsTable) {
            set(it.runId, runId)
            set(it.project, projectId)
            set(it.testSuite, suite)
            set(it.testId, testId)
            set(it.classification, classification)
        }
    }
}