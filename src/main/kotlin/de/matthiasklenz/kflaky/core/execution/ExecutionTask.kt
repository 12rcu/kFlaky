package de.matthiasklenz.kflaky.core.execution

interface ExecutionTask {
    suspend fun execute(worker: Int): Int
}