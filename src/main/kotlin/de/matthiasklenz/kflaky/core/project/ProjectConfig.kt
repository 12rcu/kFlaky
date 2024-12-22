package de.matthiasklenz.kflaky.core.project

import de.matthiasklenz.kflaky.core.strategy.TestExecutionStrategy
import de.matthiasklenz.kflaky.core.tasks.CollectResults
import java.nio.file.Path

class ProjectConfig (
    val identifier: String,
    val framworkConfig: TestFramworkConfig,
    val projectPath: Path,
    val testExecutionPath: String,
    val testResultDir: String,
    val testSrcDir: String,
    val strategy: TestExecutionStrategy,
    val testCommand: String,
    val testResultCollector: CollectResults,
    val preRuns: Int
)