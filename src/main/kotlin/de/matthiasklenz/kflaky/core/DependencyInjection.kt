package de.matthiasklenz.kflaky.core

import de.matthiasklenz.kflaky.KFlakyConfig
import de.matthiasklenz.kflaky.adapters.persistence.SqlLiteDB
import de.matthiasklenz.kflaky.core.pool.WorkerPool
import de.matthiasklenz.kflaky.core.project.ProjectConfig
import de.matthiasklenz.kflaky.core.project.ProjectProgress
import kotlinx.coroutines.channels.Channel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

fun configureDj(config: KFlakyConfig, projects:  List<ProjectConfig>) {
    val logChannel = Channel<String>()
    val terminalLogChannel = Channel<String>()
    val progressChannel = Channel<ProjectProgress>()
    val db = SqlLiteDB()
    val workerPool = WorkerPool(config.worker)

    startKoin {
        modules(
            module {
                single(qualifier = qualifier("log")) { logChannel }
                single(qualifier = qualifier("terminal")) { terminalLogChannel }
                single(qualifier = qualifier("progress")) { progressChannel }
                single { db }
                single { workerPool }
                single { config }
                single { projects }
            }
        )
    }
}