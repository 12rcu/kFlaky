package de.matthiasklenz.kflaky.core.tasks.modify

import de.matthiasklenz.kflaky.core.project.TestFramworkConfig

fun disableTestSuit(config: TestFramworkConfig, content: String): String {
    return TextModifyUtil.addFirstAnnotationBefore(content, config.testSuiteStart(), config.ignoreAnnotation())
}