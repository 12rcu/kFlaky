package de.matthiasklenz.kflaky

import org.gradle.api.Plugin
import org.gradle.api.Project

class KFlaky: Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.register("collect_results") {

        }
    }
}