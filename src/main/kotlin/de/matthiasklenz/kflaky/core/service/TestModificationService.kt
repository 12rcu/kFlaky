package de.matthiasklenz.kflaky.core.service

import de.matthiasklenz.kflaky.adapters.plattform.Platform
import de.matthiasklenz.kflaky.core.execution.TestOrders
import de.matthiasklenz.kflaky.core.middleware.KFlakyLogger
import de.matthiasklenz.kflaky.core.project.ProjectInfo
import de.matthiasklenz.kflaky.core.project.TestFrameworkConfig
import de.matthiasklenz.kflaky.core.strategy.OrderMatrix
import de.matthiasklenz.kflaky.core.tasks.TextModifyUtil
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class TestModificationService : KoinComponent {
    private val logger: KFlakyLogger by inject()

    fun writeTestOrder(projectInfo: ProjectInfo, order: TestOrders, file: File, index: Int) {
        val content = order.generatedOrders.getOrNull(index)
        if (content.isNullOrBlank()) {
            file.writeText(disableTestSuit(projectInfo.config.frameworkConfig, order.content))
        } else {
            try {
                file.writeText(content)
            } catch (e: Exception) {
                logger
                    .get("TestRunner")
                    .error("Failed to overwrite test file ${order.filePath} in ${file.absolutePath} with error: ${e.message}")
                e.printStackTrace()
                throw Exception("stop")
            }
        }
    }

    fun modifyTestOrder(runOrders: OrderMatrix, testFileContent: String, config: TestFrameworkConfig): List<String> {
        return runOrders.matrix.map {
            testFileContent
                .applyOrderAnnotationClass(config)
                .applyTestOrder(it, config)
                .applyImports(config)
        }
    }

    fun disableTestSuit(config: TestFrameworkConfig, content: String): String {
        val contentWithImports = TextModifyUtil.addImports(
            content,
            config.imports().joinToString(Platform.lineSeparator),
            config.importStart()
        )
        return TextModifyUtil.addFirstAnnotationBefore(
            contentWithImports,
            config.testSuiteStart(),
            config.ignoreAnnotation()
        )
    }

    private fun String.applyImports(config: TestFrameworkConfig): String {
        return TextModifyUtil.addImports(
            this,
            config.imports().joinToString(Platform.lineSeparator),
            config.importStart()
        )
    }

    private fun String.applyOrderAnnotationClass(config: TestFrameworkConfig): String {
        return TextModifyUtil.addFirstAnnotationBefore(this, config.testSuiteStart(), config.classOrderAnnontaion())
    }

    private fun String.applyTestOrder(order: List<Int>, config: TestFrameworkConfig): String {
        return TextModifyUtil.addAnnotationBefore(this, config.testAnnotation) { index ->
            config.testOrderAnnotation(order[index])
        }
    }
}