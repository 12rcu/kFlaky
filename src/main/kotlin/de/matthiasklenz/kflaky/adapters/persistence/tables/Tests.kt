package de.matthiasklenz.kflaky.adapters.persistence.tables

import de.matthiasklenz.kflaky.core.tasks.TestOutcome
import org.ktorm.entity.Entity
import org.ktorm.schema.*

interface DBTestResultsEntity: Entity<DBTestResultsEntity> {
    companion object : Entity.Factory<DBTestResultsEntity>()

    val id: Int
    val project: String
    val testId: String
    val runId: Int
    val result: TestOutcome
    val testOrder: String
    val testSuite: String
}

object DBTestResultsTable: Table<DBTestResultsEntity>("test_results") {
    val id = int("id").primaryKey().bindTo { it.id }
    val runId = int("run_id").bindTo { it.runId }
    val result = enum<TestOutcome>("result").bindTo { it.result }
    val project = varchar("project").bindTo { it.project }
    val testId = varchar("test_id").bindTo { it.testId }
    val testOrder = varchar("test_order").bindTo { it.testOrder }
    val testSuite = varchar("test_suite").bindTo { it.testSuite }
}