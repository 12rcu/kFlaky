package de.matthiasklenz.kflaky.jUnit

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class TestResult(
    @JacksonXmlProperty(isAttribute = false, localName = "testsuite")
    val testsuite: List<TestSuite>
)

data class TestSuite(
    @JacksonXmlProperty(isAttribute = true)
    val name: String,
    @JacksonXmlProperty(isAttribute = true)
    val tests: String,
    @JacksonXmlProperty(isAttribute = true)
    val skipped: String,
    @JacksonXmlProperty(isAttribute = true)
    val failures: String,
    @JacksonXmlProperty(isAttribute = true)
    val errors: String,
    @JacksonXmlProperty(isAttribute = true)
    val timestamp: String,
    @JacksonXmlProperty(isAttribute = true)
    val hostname: String,
    @JacksonXmlProperty(isAttribute = true)
    val time: String,
    @JacksonXmlProperty(isAttribute = false, localName = "testcase")
    val testcase: List<TestCase>
)

data class TestCase(
    @JacksonXmlProperty(isAttribute = true)
    val name: String,
    @JacksonXmlProperty(isAttribute = true)
    val classname: String,
    @JacksonXmlProperty(isAttribute = true)
    val time: String,
    @JacksonXmlProperty(isAttribute = false, localName = "failure")
    val failure: Failure? = null
)

data class Failure(
    @JacksonXmlProperty(isAttribute = true)
    val message: String,
    @JacksonXmlProperty(isAttribute = true)
    val type: String
)