package de.matthiasklenz.kflaky.core.strategy.tuscansq

import kotlinx.serialization.Serializable

@Serializable
data class TuscanCalulation(
    val size: Int,
    val matrix: List<List<Int>>
)

@Serializable
data class AllTuscanCalculations(
    val minMatrixSize: Int,
    val maxMatrixSize: Int,
    val matrixis: List<TuscanCalulation>
)