package de.matthiasklenz.kflaky.core.execution

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.qualifier
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class OsCommand: KoinComponent {
    private val terminalLog: Channel<String> by inject(qualifier("terminal"))
    private val logChannel: Channel<String> by inject(qualifier("log"))
    suspend fun executeTestCommand(command: String, directory: File, worker: String) = coroutineScope {
        val processBuilder = ProcessBuilder(command.split(" "))
        processBuilder.directory(directory)
        processBuilder.redirectErrorStream(true)

        terminalLog.send("[$worker] Execute $command in ${directory.absolutePath}")

        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        reader.forEachLine { line ->
            launch {
                logChannel.send("[$worker] " + line.trim())
            }
        }

        val exitCode = process.waitFor()
        terminalLog.send("[$worker] Command $command in ${directory.absolutePath} exited with code: $exitCode")
        if (exitCode != 0)
            logChannel.send("[$worker] ERROR: exitcode $exitCode for command: $command in dir: ${directory.absolutePath}")
    }
}
