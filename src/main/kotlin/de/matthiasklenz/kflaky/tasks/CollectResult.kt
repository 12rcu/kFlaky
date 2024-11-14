package de.matthiasklenz.kflaky.tasks

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import de.matthiasklenz.kflaky.jUnit.TestResult
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

public abstract class CollectResult: DefaultTask() {
    @TaskAction
    fun collect() {
        dependsOn("test")   //run test once to generate initial results
        val mapper = XmlMapper()
        val results = project
            .layout
            .buildDirectory
            .dir("test-results")
            .get()
            .asFile
            .walk()
            .filter { it.isFile && it.name.endsWith(".xml") }
            .map { mapper.readValue(it.readText(), TestResult::class.java) }

    }
}