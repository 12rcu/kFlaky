package de.matthiasklenz.kflaky.core.pool

interface ExecutionTask {
    suspend fun execute(worker: Int)
}