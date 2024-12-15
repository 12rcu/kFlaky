package de.matthiasklenz.kflaky.adapters.persistence.tables

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.timestamp
import org.ktorm.schema.varchar
import java.time.Instant

interface DBRunsEntity: Entity<DBRunsEntity> {
    companion object : Entity.Factory<DBRunsEntity>()

    val id: Int
    val startTime: Instant
    val projects: String
}

object DBRunsTable: Table<DBRunsEntity>("runs") {
    val id = int("id").primaryKey().bindTo { it.id }
    val startTime = timestamp("start_time").bindTo { it.startTime }
    val projects = varchar("projects").bindTo { it.projects }
}