package de.matthiasklenz.kflaky.core.tasks.modify

import de.matthiasklenz.kflaky.core.plattform.Plattform
import de.matthiasklenz.kflaky.core.project.TestFramworkConfig
import de.matthiasklenz.kflaky.core.strategy.tuscansq.TuscanCalculation
import de.matthiasklenz.kflaky.core.strategy.tuscansq.TuscanGenerator

fun tuskanSquareOrderGenerate(config: TestFramworkConfig, testFileContent: String): TuscanCalculation {
    val tests = config.testAnnotation.findAll(testFileContent)
    val testCount = tests.count()

    if (testCount == 1) {
        return TuscanCalculation(
            1,
            listOf(listOf(1))
        )
    }

    val generator = TuscanGenerator()
    val runOrders = generator.generate(testCount)
    return runOrders
}

fun tuskanSquareModify(runOrders: TuscanCalculation, testFileContent: String, config: TestFramworkConfig): List<String> {
    return runOrders.matrix.map {
        testFileContent
            .applyOrderAddnotationClass(config)
            .applyTestOrder(it, config)
            .applyImports(config)
    }
}

private fun String.applyImports(config: TestFramworkConfig): String {
    return TextModifyUtil.addImports(this, config.imports.joinToString(Plattform.lineSeperator))
}

private fun String.applyOrderAddnotationClass(config: TestFramworkConfig): String {
    return TextModifyUtil.addFirstAnnotationBefore(this, Regex("class"), config.classOrderAnnontaion())
}

private fun String.applyTestOrder(order: List<Int>, config: TestFramworkConfig): String {
    return TextModifyUtil.addAnnotationBefore(this, config.testAnnotation) { index ->
        config.testOrderAnnotation(order[index])
    }
}