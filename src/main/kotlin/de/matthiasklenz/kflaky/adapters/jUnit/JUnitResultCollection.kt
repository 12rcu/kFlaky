package de.matthiasklenz.kflaky.adapters.jUnit

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import de.matthiasklenz.kflaky.core.tasks.CollectResults
import de.matthiasklenz.kflaky.core.tasks.TestOutcome
import de.matthiasklenz.kflaky.core.tasks.TestResultData
import de.matthiasklenz.kflaky.core.tasks.TestSuiteResultInfo
import java.nio.file.Path

class JUnitResultCollection : CollectResults {
    override fun collect(
        resultsPath: Path,
        getTestOrderOf: (testSuiteId: String) -> List<Int>
    ): List<TestSuiteResultInfo> {
        val mapper = XmlMapper()
        val files = resultsPath
            .toFile()
            .walk()
            .filter { it.isFile && it.name.startsWith("TEST-") && it.name.endsWith(".xml") }

        val results = files
            .map { it.readText() }
            .map {
                try {
                    mapper.readValue(it, TestSuite::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                    throw RuntimeException(e)
                } to it
            }
            .filter { it.first != null }

        val ret = results.map { testSuite ->
            val result = TestSuiteResultInfo(
                testSuite.first!!.name,
                testSuite.second,
                getTestOrderOf(testSuite.first!!.name),
                mutableListOf()
            )
            testSuite.first?.testcase?.map { case ->
                val outcome = if (case.failure == null) TestOutcome.PASSED else TestOutcome.FAILED
                result.testResults.add(
                    TestResultData(
                        case.name,
                        outcome
                    )
                )
            }
            result
        }.toList()
        return ret
    }
}