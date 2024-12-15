package de.matthiasklenz.kflaky.core.execution

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.qualifier
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class OsCommand: KoinComponent {
    private val logChannel: Channel<String> by inject(qualifier("log"))
    suspend fun executeTestCommand(command: String, directory: File) = coroutineScope {
        val processBuilder = ProcessBuilder(command.split(" "))
        processBuilder.directory(directory)
        processBuilder.redirectErrorStream(true)

        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        reader.forEachLine { line ->
            launch {
                logChannel.send(line.trim())
            }
        }

        val exitCode = process.waitFor()
        if (exitCode != 0)
            logChannel.send("ERROR: exitcode $exitCode for command: $command in dir: ${directory.absolutePath}")
    }
}
