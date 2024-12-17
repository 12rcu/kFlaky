package de.matthiasklenz.kflaky.adapters.persistence.tables

import de.matthiasklenz.kflaky.core.detection.FlakyClassification
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.enum
import org.ktorm.schema.int
import org.ktorm.schema.varchar


interface DBTestClassificationEntity : Entity<DBTestClassificationEntity> {
    companion object : Entity.Factory<DBTestClassificationEntity>()

    val id: Int
    val runId: Int
    val project: String
    val testId: String
    val testSuite: String
    val classification: FlakyClassification
}

object DBTestClassificationsTable : Table<DBTestClassificationEntity>("test_classifications") {
    val id = int("id").primaryKey().bindTo { it.id }
    val runId = int("run_id").bindTo { it.runId }
    val project = varchar("project").bindTo { it.project }
    val testSuite = varchar("test_suite").bindTo { it.testSuite }
    val testId = varchar("test_id").bindTo { it.testId }
    val classification = enum<FlakyClassification>("classification").bindTo { it.classification }
}