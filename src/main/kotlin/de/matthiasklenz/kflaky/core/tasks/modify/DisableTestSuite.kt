package de.matthiasklenz.kflaky.core.tasks.modify

import de.matthiasklenz.kflaky.core.plattform.Plattform
import de.matthiasklenz.kflaky.core.project.TestFramworkConfig

fun disableTestSuit(config: TestFramworkConfig, content: String): String {
    val contentWithImports = TextModifyUtil.addImports(content, config.imports().joinToString(Plattform.lineSeperator), config.importStart())
    return TextModifyUtil.addFirstAnnotationBefore(contentWithImports, config.testSuiteStart(), config.ignoreAnnotation())
}