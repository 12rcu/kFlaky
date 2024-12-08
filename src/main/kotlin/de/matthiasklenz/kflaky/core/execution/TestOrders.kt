package de.matthiasklenz.kflaky.core.execution

import java.io.File

data class TestOrders(
    val file: File,
    val content: String,
    val generatedOrders: List<String>
)