package de.matthiasklenz.kflaky.core.service

import de.matthiasklenz.kflaky.core.middleware.KFlakyLogger
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class GitService: KoinComponent {
    val logger: KFlakyLogger by inject()

    /**
     * clones a repo with git to the target dir.
     * @return the status code of the clone command
     */
    suspend fun download(url: String, targetDir: File): Int = coroutineScope {
        val l = logger.get("GitService")

        if(targetDir.exists()) {
            targetDir.deleteRecursively()
        }
        targetDir.mkdirs()

        val processBuilder = ProcessBuilder("git", "clone", url, ".")
        processBuilder.directory(targetDir)
        val process = processBuilder.start()

        val reader = BufferedReader(InputStreamReader(process.inputStream))

        reader.forEachLine { line ->
            launch {
                l.debug(line.trim())
            }
        }

        val code = process.waitFor()
        return@coroutineScope code
    }
}