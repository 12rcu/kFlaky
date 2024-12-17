package de.matthiasklenz.kflaky.adapters.persistence

import kotlinx.coroutines.channels.Channel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.qualifier
import java.io.File

class KFlakyLogger(private val file: File): KoinComponent {
    private val logChannel: Channel<String> by inject(qualifier("log"))
    private var project = "unknown"

    init {
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