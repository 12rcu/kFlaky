package de.matthiasklenz.kflaky.adapters.jUnit

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import de.matthiasklenz.kflaky.core.tasks.CollectResults
import de.matthiasklenz.kflaky.core.tasks.TestOutcome
import de.matthiasklenz.kflaky.core.tasks.TestResultData
import java.nio.file.Path

class JUnitResultCollection : CollectResults {
    override fun collect(resultsPath: Path): List<TestResultData> {
        val mapper = XmlMapper()
        val results = resultsPath
            .toFile()
            .walk()
            .filter { it.isFile && it.name.endsWith(".xml") }
            .map { mapper.readValue(it.readText(), TestSuite::class.java) }
        val res = results.map { testSuite ->
            testSuite.testcase?.map { case ->
                val outcome = if(case.failure == null) TestOutcome.PASSED else TestOutcome.FAILED
                TestResultData(
                    case.name,
                    outcome
                )
            }?.toList() ?: listOf()
        }.toList().flatten()
        return res
    }
}