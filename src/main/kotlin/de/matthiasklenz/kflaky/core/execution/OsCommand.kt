package de.matthiasklenz.kflaky.core.execution

import de.matthiasklenz.kflaky.core.middleware.KFlakyLogger
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.file.attribute.PosixFilePermission
import kotlin.io.path.Path
import kotlin.io.path.setPosixFilePermissions

class OsCommand: KoinComponent {
    private val logger: KFlakyLogger by inject()

    suspend fun executeTestCommand(command: String, directory: File, worker: String) = coroutineScope {
        val l = logger.get("cmd")

        val processBuilder = ProcessBuilder(command.split(" "))
        processBuilder.directory(directory)
        processBuilder.redirectErrorStream(true)

        l.info("[$worker] Execute $command in ${directory.absolutePath}")

        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        reader.forEachLine { line ->
            launch {
                l.debug("[$worker] " + line.trim())
            }
        }

        val exitCode = process.waitFor()
        l.info("[$worker] Command $command in ${directory.absolutePath} exited with code: $exitCode")
        if (exitCode != 0)
            l.warn("[$worker] ERROR: exit code $exitCode for command: $command in dir: ${directory.absolutePath}")
    }

    suspend fun setExecPerms(command: String, directory: File) {
        val file = command.split(" ").firstOrNull() ?: ""
        val execFile =  if(file.startsWith(".")) {
            directory.toPath().resolve(file)
        } else {
            Path(file)
        }

        val isWindows = System.getProperty("os.name").lowercase().startsWith("windows");

        if (!isWindows) {
            execFile.setPosixFilePermissions(
                setOf(
                    PosixFilePermission.GROUP_EXECUTE,
                    PosixFilePermission.GROUP_READ,
                    PosixFilePermission.GROUP_WRITE,
                    PosixFilePermission.OTHERS_EXECUTE,
                    PosixFilePermission.OTHERS_READ,
                    PosixFilePermission.OTHERS_WRITE,
                    PosixFilePermission.OWNER_EXECUTE,
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE
                )
            )
        }
    }
}
