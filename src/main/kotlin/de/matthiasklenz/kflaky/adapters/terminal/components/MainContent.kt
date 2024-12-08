package de.matthiasklenz.kflaky.adapters.terminal.components

import de.matthiasklenz.kflaky.adapters.terminal.Apperance
import de.matthiasklenz.kflaky.adapters.terminal.centerAndContain
import de.matthiasklenz.kflaky.adapters.terminal.centerSring
import de.matthiasklenz.kflaky.adapters.terminal.containLength
import de.matthiasklenz.kflaky.core.project.ProjectProgress
import de.matthiasklenz.kflaky.core.project.ProjectState
import org.fusesource.jansi.Ansi.ansi

/**
 * @param projects, if there is not enough space for all projects, the current project will be centered (not implemented)
 * @param width the width of the terminal
 * @param bodyHeight the height this component should take up (to leave space for the header and footer)
 */
fun printMainConent(projects: List<ProjectProgress>, width: Int, bodyHeight: Int, projectDevision: Int, testDevision: Int) {
    for (i in 0 until bodyHeight) {
        val project = projects.getOrNull(i)
        val projectNameState = project?.name?.plus(" (${project.state})")
        val projectName = centerAndContain(projectNameState ?: "", projectDevision - 1, ' ')

        val test = projects.firstOrNull { it.state == ProjectState.RUNNING }?.testProgress?.getOrNull(i)
        val testName = centerAndContain(test?.name ?: "", testDevision - projectDevision - 1, ' ')

        val progressSpace = width - testDevision - 2
        val progressContent = containLength("tba", progressSpace) //make sure the name fits!
        val progress = centerSring(content = progressContent, progressSpace, ' ')

        println(
            ansi()
                .fgRgb(Apperance.BG)
                .a("║")
                .fgRgb(project?.state?.getColor() ?: Apperance.SECONDARY)
                .a(projectName)
                .fgRgb(Apperance.BG)
                .a("║")
                .fgRgb(Apperance.SECONDARY)
                .a(testName)
                .fgRgb(Apperance.BG)
                .a("║")
                .fgRgb(Apperance.SECONDARY)
                .a(progress)
                .fgRgb(Apperance.BG)
                .a("║")
        )
    }
}

private fun ProjectState.getColor(): Int {
    return when (this.name) {
        "SETUP" -> 0xC2C2C2
        "RUNNING" -> 0x4C83DF
        "EVAL" -> 0x75BAB6
        "CLEANUP" -> 0x4BBC85
        "DONE" -> 0x4BBC85
        else -> 0xC2C2C2
    }
}