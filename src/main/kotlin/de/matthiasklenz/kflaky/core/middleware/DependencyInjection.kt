package de.matthiasklenz.kflaky.core.middleware

import de.matthiasklenz.kflaky.adapters.persistence.SqlLiteDB
import de.matthiasklenz.kflaky.core.project.ProjectInfo
import de.matthiasklenz.kflaky.core.service.*
import okhttp3.OkHttpClient
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

fun configureDj(initModule: Module) {
    startKoin {
        modules(
            initModule,
            module {
                single { KFlakyLogger() }
                single { SqlLiteDB() }
                single { OkHttpClient() }
                single(qualifier("projects")) { hashMapOf<String, ProjectInfo>() }

                single { DynamicProjectService() }
                single { FlakyDetectionService() }
                single { GitService() }
                single { TestModificationService() }
                single { OdSchedulerService() }
                single { PreRunSchedulerService() }
                single { WorkerService() }
                single { ClassificationService() }
                single { ProjectValidationService() }
                single { PairWiseSchedulerService() }
            }
        )
    }
}