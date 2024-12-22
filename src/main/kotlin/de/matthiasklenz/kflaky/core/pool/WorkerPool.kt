package de.matthiasklenz.kflaky.core.pool

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.util.concurrent.ConcurrentHashMap

class WorkerPool(val workerCount: Int) {

    private  var channel: Channel<ExecutionTask> = Channel()
    private val jobs = mutableListOf<Job>()
    private val jobStatus: ConcurrentHashMap<Int, Boolean> = ConcurrentHashMap()
    private val workers = Semaphore(workerCount)
    private val lockJobs = Semaphore(1)

    suspend fun start() = coroutineScope {
        channel = Channel()
        launch {
            lockJobs.acquire()
            for (task in channel) {
                val job = launch(Dispatchers.IO) {
                    workers.withPermit {
                        val jobId = synchronized(jobStatus) {
                            val id = jobStatus.map { it.key to it.value }.firstOrNull { it.second }?.first ?: jobStatus.size
                            jobStatus[id] = false
                            id
                        }
                        task.execute(jobId)
                        jobStatus[jobId] = true //finished, directory can be taken by other coroutine
                    }
                }
                jobs.add(job)
            }
            lockJobs.release()
        }
    }

    suspend fun execute(task: ExecutionTask) {
        channel.send(task)
    }

    suspend fun join() {
        lockJobs.acquire()
        jobs.forEach {
            it.join()
        }
        lockJobs.release()
    }

    fun close() = channel.close()
}