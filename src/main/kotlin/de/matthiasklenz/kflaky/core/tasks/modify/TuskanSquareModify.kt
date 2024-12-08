package de.matthiasklenz.kflaky.core.tasks.modify

import de.matthiasklenz.kflaky.core.plattform.Plattform
import de.matthiasklenz.kflaky.core.project.TestFramworkConfig
import de.matthiasklenz.kflaky.core.strategy.tuscansq.TuscanGenerator

fun tuskanSquareModify(config: TestFramworkConfig, testFileContent: String): List<String> {
    val tests = config.testAnnotation.findAll(testFileContent)
    val testCount = tests.count()

    if (testCount == 1) {
        return listOf(testFileContent)
    }

    val generator = TuscanGenerator()
    val runOrders = generator.generate(testCount)

    return runOrders.matrix.map {
        testFileContent
            .applyOrderAddnotationClass(config)
            .applyTestOrder(it, config)
            .applyImports(config)
    }
}

private fun String.applyImports(config: TestFramworkConfig): String {
    return TextModifyUtil.addImports(this, config.collectedImports.joinToString { Plattform.lineSeperator })
}

private fun String.applyOrderAddnotationClass(config: TestFramworkConfig): String {
    return TextModifyUtil.addFirstAnnotationBefore(this, Regex("class"), config.classOrderAnnontaion())
}

private fun String.applyTestOrder(order: List<Int>, config: TestFramworkConfig): String {
    return TextModifyUtil.addAnnotationBefore(this, config.testAnnotation) { index ->
        config.testOrderAnnotation(order[index])
    }
}