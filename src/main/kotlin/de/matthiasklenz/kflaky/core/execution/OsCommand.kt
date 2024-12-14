package de.matthiasklenz.kflaky.core.execution

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
        processBuilder.redirectErrorStream(true)
        println("Executing command: $command in directory: ${directory.absolutePath}")

        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        reader.forEachLine { line ->
            launch {
                output.send(line.trim())
            }
        }

        val exitCode = process.waitFor()
        if (exitCode != 0)
            println("ERROR: exitcode $exitCode")
    }
}
