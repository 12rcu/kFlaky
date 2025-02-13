package de.matthiasklenz.kflaky.core.project

import de.matthiasklenz.kflaky.core.classification.TestClassificationInfo
import de.matthiasklenz.kflaky.core.tasks.TestOutcomeInfo

data class ProjectInfo (
    val config: ProjectConfig,
    val progress: ProjectProgress,
    var results: List<TestOutcomeInfo> = listOf(),
    var classification: List<TestClassificationInfo> = listOf()
)