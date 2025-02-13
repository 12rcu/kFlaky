package de.matthiasklenz.kflaky.core.execution

import de.matthiasklenz.kflaky.core.middleware.KFlakyLogger
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class OsCommand: KoinComponent {
    private val logger: KFlakyLogger by inject()

    suspend fun executeTestCommand(command: String, directory: File, worker: String) = coroutineScope {
        val l = logger.get("cmd")

        val processBuilder = ProcessBuilder(command.split(" "))
        processBuilder.directory(directory)
        processBuilder.redirectErrorStream(true)

        l.log("[$worker] Execute $command in ${directory.absolutePath}")

        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        reader.forEachLine { line ->
            launch {
                l.debug("[$worker] " + line.trim())
            }
        }

        val exitCode = process.waitFor()
        l.log("[$worker] Command $command in ${directory.absolutePath} exited with code: $exitCode")
        if (exitCode != 0)
            l.warn("[$worker] ERROR: exit code $exitCode for command: $command in dir: ${directory.absolutePath}")
    }
}
