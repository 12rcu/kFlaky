package de.matthiasklenz.kflaky.core.service

import de.matthiasklenz.kflaky.adapters.github.GithubRepoResponse
import de.matthiasklenz.kflaky.adapters.github.GithubRequest
import de.matthiasklenz.kflaky.core.middleware.KFlakyLogger
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class GitService : KoinComponent {
    private val logger: KFlakyLogger by inject()
    private val client: OkHttpClient by inject()

    /**
     * clones a repo with git to the target dir.
     * @return the status code of the clone command
     */
    suspend fun download(url: String, targetDir: File): Int = coroutineScope {
        val l = logger.get("GitService")

        if (targetDir.exists()) {
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

    fun searchRepositories(githubRequest: GithubRequest): GithubRepoResponse? {
        val l = logger.get("GitService")
        val request = Request.Builder()
            .url("https://api.github.com/search/repositories?${githubRequest.query}")
            .addHeader("Accept", "application/vnd.github.v3+json")
            .addHeader("Authorization", "Bearer ${githubRequest.token}")
            .build()

        return try {
            val json = Json {
                ignoreUnknownKeys = true
            }
            json.decodeFromString<GithubRepoResponse>(client.newCall(request).execute().body!!.string())
        } catch (e: Exception) {
            l.warn(e.message.toString())
            e.printStackTrace()
            null
        }
    }
}