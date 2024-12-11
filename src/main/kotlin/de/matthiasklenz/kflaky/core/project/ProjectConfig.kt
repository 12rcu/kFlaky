package de.matthiasklenz.kflaky.core.project

import de.matthiasklenz.kflaky.core.strategy.TestExecutionStrategy
import java.nio.file.Path

class ProjectConfig (
    val framworkConfig: TestFramworkConfig,
    val testResultDir: Path,
    val testSrcDir: Path,
    val strategy: TestExecutionStrategy
)