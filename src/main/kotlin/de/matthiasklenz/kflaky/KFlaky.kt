package de.matthiasklenz.kflaky

import de.matthiasklenz.kflaky.tasks.CollectResult
import org.gradle.api.Plugin
import org.gradle.api.Project

class KFlaky: Plugin<Project> {
    override fun apply(target: Project) {
        println("Apply kFlaky!")

        target.tasks.register("collect", CollectResult::class.java) { task ->
            task.dependsOn("test")
            task.group = "kFlaky"
        }
    }
}