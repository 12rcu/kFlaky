package de.matthiasklenz.kflaky.core.strategy

import de.matthiasklenz.kflaky.core.project.TestFrameworkConfig
import de.matthiasklenz.kflaky.core.strategy.OrderMatrix
import de.matthiasklenz.kflaky.core.strategy.tuscansq.TuscanGenerator

fun tuskanSquareOrderGenerate(config: TestFrameworkConfig, testFileContent: String): OrderMatrix {
    val tests = config.testAnnotation.findAll(testFileContent)
    val testCount = tests.count()

    if (testCount == 1) {
        return OrderMatrix(
            1,
            listOf(listOf(1))
        )
    }

    val generator = TuscanGenerator()
    val runOrders = generator.generate(testCount)
    return runOrders
}

fun reverseOrderGenerate(tests: Int, config: TestFrameworkConfig, testFileContent: String): OrderMatrix {
    val order = mutableListOf<Int>()
    for (i in tests downTo 1) {
        order.add(i)
    }
    return OrderMatrix(1, listOf(order))
}