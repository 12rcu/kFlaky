package de.matthiasklenz.kflaky.core.tasks

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import de.matthiasklenz.kflaky.adapters.jUnit.TestSuite
import java.nio.file.Path

class CollectResult {
    fun collect(testResultsDir: Path) {
        val mapper = XmlMapper()
        val results = testResultsDir
            .toFile()
            .walk()
            .filter { it.isFile && it.name.endsWith(".xml") }
            .map { mapper.readValue(it.readText(), TestSuite::class.java) }
        results.forEach { testSuite ->
            println("Test Suite ${testSuite.name}")
            testSuite.testcase?.forEach { case ->
                println("Case ${case.name}")
                println("Passed: ${case.failure == null}")
            }
        }
    }
}