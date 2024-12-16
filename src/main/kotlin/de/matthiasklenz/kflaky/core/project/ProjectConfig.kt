package de.matthiasklenz.kflaky.core.project

import de.matthiasklenz.kflaky.core.strategy.TestExecutionStrategy
import de.matthiasklenz.kflaky.core.tasks.CollectResults
import java.nio.file.Path

class ProjectConfig (
    val identifier: String,
    val framworkConfig: TestFramworkConfig,
    val testResultDir: Path,
    val testSrcDir: Path,
    val strategy: TestExecutionStrategy,
    val testCommand: String,
    val testExecutionPath: Path,
    val testResultCollector: CollectResults,
    val preRuns: Int
)