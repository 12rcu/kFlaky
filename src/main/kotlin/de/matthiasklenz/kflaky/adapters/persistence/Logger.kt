package de.matthiasklenz.kflaky.adapters.persistence

import de.matthiasklenz.kflaky.KFlakyConfig
import kotlinx.coroutines.channels.Channel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.qualifier
import java.io.File

class KFlakyLogger(config: KFlakyConfig, runId: Int): KoinComponent {
    private val logChannel: Channel<String> by inject(qualifier("log"))
    private var project = "unknown"

    private val file = config.logDir.resolve("log-$runId").toFile()

    init {
        config.logDir.toFile().mkdirs()
        if(!file.exists()) {
            file.createNewFile()
        } else {
            file.delete()
            file.createNewFile()
        }
    }

    fun setProject(name: String) {
        project = name
    }

    suspend fun startWriting() {
        for (log in logChannel) {
            file.appendText("[$project] $log \n")
        }
    }
}