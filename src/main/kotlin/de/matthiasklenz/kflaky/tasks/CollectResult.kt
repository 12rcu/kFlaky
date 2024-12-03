package de.matthiasklenz.kflaky.tasks

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import de.matthiasklenz.kflaky.jUnit.TestSuite
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.tooling.GradleConnectionException
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ResultHandler

public abstract class CollectResult : DefaultTask() {
    @TaskAction
    fun collect() {

        val connector = GradleConnector.newConnector()
        connector.forProjectDirectory(project.projectDir)
        val connection = connector.connect()
        connection.newBuild()
        val testLauncher = connection.newTestLauncher()
        testLauncher.run(TestHandler())

        val mapper = XmlMapper()
        val results = project
            .layout
            .buildDirectory
            .dir("test-results")
            .get()
            .asFile
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

class TestHandler: ResultHandler<Any> {
    override fun onComplete(result: Any?) {
        println("Success: $result")
    }

    override fun onFailure(failure: GradleConnectionException?) {
        println("Failure: ${failure?.message}")
    }
}