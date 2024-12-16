package de.matthiasklenz.kflaky.adapters.project

import de.matthiasklenz.kflaky.core.strategy.TestExecutionStrategy
import kotlinx.serialization.Serializable

@Serializable
data class ProjectConfigDto(
    val identifier: String,
    val framework: String,
    val language: String,
    val testExecutionCommand: String,
    val testExecutionDir: String,
    val testDir: String,
    val testResultDir: String,
    val strategy: TestExecutionStrategy,
    val preRuns: Int
)