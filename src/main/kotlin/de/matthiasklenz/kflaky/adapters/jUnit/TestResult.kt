package de.matthiasklenz.kflaky.adapters.jUnit

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText

@JacksonXmlRootElement(localName = "testsuite")
class TestSuite {
    @JacksonXmlProperty(isAttribute = true)
    var name: String = ""

    @JacksonXmlProperty(isAttribute = true)
    var tests: String = ""

    @JacksonXmlProperty(isAttribute = true)
    var skipped: String = ""

    @JacksonXmlProperty(isAttribute = true)
    var failures: String = ""

    @JacksonXmlProperty(isAttribute = true)
    var errors: String = ""

    @JacksonXmlProperty(isAttribute = true)
    var timestamp: String = ""

    @JacksonXmlProperty(isAttribute = true)
    var hostname: String = ""

    @JacksonXmlProperty(isAttribute = true)
    var time: String = ""

    var properties: TestSuiteProperties? = null

    @JacksonXmlProperty(localName = "system-out")
    var systemOut: String? = null


    @JacksonXmlProperty(localName = "system-err")
    var systemErr: String? = null

    @JacksonXmlElementWrapper(useWrapping = false, localName = "testcase")
    var testcase: List<TestCase>? = null
}

class TestSuiteProperties

class TestCase {
    @JacksonXmlProperty(isAttribute = true)
    var name: String = ""

    @JacksonXmlProperty(isAttribute = true)
    var classname: String = ""

    @JacksonXmlProperty(isAttribute = true)
    var time: String = ""

    @JacksonXmlProperty(isAttribute = false, localName = "failure")
    var failure: Failure? = null
}

class Failure {
    @JacksonXmlProperty(isAttribute = true)
    var message: String = ""

    @JacksonXmlProperty(isAttribute = true)
    var type: String = ""

    @JacksonXmlText(value = true)
    var text: String? = null
}