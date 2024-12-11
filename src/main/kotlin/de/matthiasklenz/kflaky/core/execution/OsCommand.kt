package de.matthiasklenz.kflaky.core.execution

import de.matthiasklenz.kflaky.core.logging.getLogger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class OsCommand(private val output: Channel<String>) {
    suspend fun executeTestCommand(command: String, directory: File) = coroutineScope {
        val processBuilder = ProcessBuilder(command.split(" "))
        processBuilder.directory(directory)

        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        launch {
            reader.forEachLine {  }
            reader.forEachLine {
                runCatching {
                    launch {
                        output.send(it)
                    }
                }.onFailure {
                    getLogger("Test Command").error(it.localizedMessage)
                }
            }
        }

        val exitCode = process.waitFor()
        output.close()
    }

    private suspend fun sendOutput(data: String) = coroutineScope {
        output.send(data)
    }
}
