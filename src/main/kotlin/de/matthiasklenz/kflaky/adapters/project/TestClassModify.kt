package de.matthiasklenz.kflaky.adapters.project

import de.matthiasklenz.kflaky.core.strategy.TestExecutionStrategy
import java.io.File

class TestClassModify {
    private val content: String
    private val tests: Int
    private val testRegex = Regex("@Test")

    constructor(file: File) {
        content = file.readText()
        tests = testRegex.findAll(content).count()
    }

    constructor(content: String) {
        this.content = content
        tests = testRegex.findAll(content).count()
    }

    /**
     * reorders tests for a given strategy and return all possible tests
     */
    fun generateReorderTests(strategy: TestExecutionStrategy): List<String> {
        return listOf()
    }
}