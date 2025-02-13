package de.matthiasklenz.kflaky.core.strategy

import de.matthiasklenz.kflaky.core.execution.TestOrderMatrix
import kotlinx.serialization.Serializable

@Serializable
data class OrderMatrix(
    override val size: Int,
    override val matrix: List<List<Int>>
): TestOrderMatrix

@Serializable
data class AllTuscanCalculations(
    val minMatrixSize: Int,
    val maxMatrixSize: Int,
    val matrixis: List<OrderMatrix>
)