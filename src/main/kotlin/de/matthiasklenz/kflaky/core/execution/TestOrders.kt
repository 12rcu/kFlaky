package de.matthiasklenz.kflaky.core.execution

import de.matthiasklenz.kflaky.core.strategy.tuscansq.TuscanCalculation
import java.io.File

data class TestOrders(
    val file: File,
    val content: String,
    val generatedOrders: List<String>,
    val orders: TuscanCalculation
)