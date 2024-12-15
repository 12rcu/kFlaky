package de.matthiasklenz.kflaky.adapters.terminal.components

import de.matthiasklenz.kflaky.adapters.terminal.Appearance
import de.matthiasklenz.kflaky.adapters.terminal.centerSring
import de.matthiasklenz.kflaky.adapters.terminal.containLength
import org.fusesource.jansi.Ansi.ansi

fun printHeader(width: Int, projectDevision: Int, testDevision: Int) {
    val kFlaky = StringBuilder()
    for (i in 0 until width) kFlaky.append(" ")
    val start = width / 2 - 3
    kFlaky.replace(start, start + 6, "kFlaky")
    println(ansi().fgRgb(Appearance.PRIMARY).bold().a(kFlaky.toString()).reset())

    val top = StringBuilder()
    for (i in 0 until width) top.append("═")
    top.replace(0, 1, "╔")
    top.replace(width - 1, width, "╗")
    top.replace(projectDevision, projectDevision + 1, "╦")
    top.replace(testDevision, testDevision + 1, "╦")
    println(ansi().fgRgb(Appearance.BG).a(top.toString()).reset())

    val projects = centerSring("Projects:", projectDevision - 1, ' ')
    val tests = centerSring("Progress:",  testDevision - projectDevision - 1, ' ')
    val progressSpace = width - testDevision - 2
    val progressContent = containLength("Log:", progressSpace) //make sure the name fits!
    val progress = centerSring(content = progressContent, progressSpace, ' ')
    println(
        ansi()
            .fgRgb(Appearance.BG)
            .a("║")
            .fgRgb(Appearance.SECONDARY)
            .a(projects)
            .fgRgb(Appearance.BG)
            .a("║")
            .fgRgb(Appearance.SECONDARY)
            .a(tests)
            .fgRgb(Appearance.BG)
            .a("║")
            .fgRgb(Appearance.SECONDARY)
            .a(progress)
            .fgRgb(Appearance.BG)
            .a("║")
    )

    val bottom = StringBuilder()
    for (i in 0 until width) bottom.append("─")
    bottom.replace(0, 1, "╟")
    bottom.replace(width - 1, width, "╢")
    bottom.replace(projectDevision, projectDevision + 1, "╫")
    bottom.replace(testDevision, testDevision + 1, "╫")
    println(ansi().fgRgb(Appearance.BG).a(bottom.toString()).reset())

}