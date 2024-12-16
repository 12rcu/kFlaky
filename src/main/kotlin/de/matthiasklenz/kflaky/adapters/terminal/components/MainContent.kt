package de.matthiasklenz.kflaky.adapters.terminal.components

import de.matthiasklenz.kflaky.adapters.terminal.Appearance
import de.matthiasklenz.kflaky.adapters.terminal.appendWhitespaceToString
import de.matthiasklenz.kflaky.adapters.terminal.centerAndContain
import de.matthiasklenz.kflaky.adapters.terminal.containLength
import de.matthiasklenz.kflaky.core.project.ProjectProgress
import de.matthiasklenz.kflaky.core.project.ProjectState
import org.fusesource.jansi.Ansi.ansi
import kotlin.math.roundToInt

/**
 * @param projects, if there is not enough space for all projects, the current project will be centered (not implemented)
 * @param width the width of the terminal
 * @param bodyHeight the height this component should take up (to leave space for the header and footer)
 */
fun printMainConent(projects: List<ProjectProgress>, debug: List<String>, width: Int, bodyHeight: Int, projectDevision: Int, progressDevision: Int) {
    for (i in 0 until bodyHeight) {
        val project = projects.getOrNull(i)
        val projectNameState = project?.name?.plus(" (${project.state})")
        val projectName = centerAndContain(projectNameState ?: "", projectDevision - 1, ' ')

        val testProgessSpace = progressDevision - projectDevision - 2
        val testProgressFraction = if(project != null && project.testsToRun != 0) project.index / project.testsToRun.toFloat() else null
        val testProgressCutoff = if(testProgressFraction != null) (testProgressFraction * testProgessSpace).toInt() else null

        val progress = StringBuilder(" ")
        if(testProgressCutoff != null) {
            for (j in 1 until minOf(testProgressCutoff, testProgessSpace)) {
                progress.append("█")
            }

            for (j in maxOf(testProgressCutoff, 1) until testProgessSpace) {
                progress.append("░")
            }
            progress.append(" ")
        } else {
            for (j in 0 until testProgessSpace) {
                progress.append(" ")
            }
        }

        val log = debug.getOrNull(debug.size - bodyHeight + i) ?: ""
        val logStrContained = containLength(log ?: "", width - progressDevision - 3)
        val logStrSized = appendWhitespaceToString(logStrContained, width - progressDevision - 3)
        val logOut = StringBuilder(" ")
        logOut.append(logStrSized)

        println(
            ansi()
                .fgRgb(Appearance.BG)
                .a("║")
                .fgRgb(project?.state?.getColor() ?: Appearance.SECONDARY)
                .a(projectName)
                .fgRgb(Appearance.BG)
                .a("║")
                .fgRgb(Appearance.PROGRESS)
                .a(progress)
                .fgRgb(Appearance.BG)
                .a("║")
                .fgRgb(Appearance.SECONDARY)
                .a(logOut)
                .fgRgb(Appearance.BG)
                .a("║")
        )
    }
}

private fun ProjectState.getColor(): Int {
    return when (this.name) {
        "SETUP" -> Appearance.SECONDARY
        "PRE_RUNS" -> Appearance.INFO
        "RUNNING" -> Appearance.INFO
        "EVAL" -> Appearance.ERR
        "CLEANUP" -> Appearance.ERR
        "DONE" -> Appearance.SUCCESS
        else ->  Appearance.SECONDARY
    }
}