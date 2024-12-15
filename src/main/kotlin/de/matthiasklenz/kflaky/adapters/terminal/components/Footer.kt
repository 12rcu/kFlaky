package de.matthiasklenz.kflaky.adapters.terminal.components

import de.matthiasklenz.kflaky.adapters.terminal.Appearance
import org.fusesource.jansi.Ansi.ansi

fun printFooter(width: Int, projectDevision: Int, testDevision: Int) {
    val bar = StringBuilder()
    for (i in 0 until width) bar.append("═")
    bar.replace(0, 1, "╚")
    bar.replace(width - 1, width, "╝")
    bar.replace(projectDevision, projectDevision + 1, "╩")
    bar.replace(testDevision, testDevision + 1, "╩")
    print(ansi().fgRgb(Appearance.BG).a(bar.toString()).reset())
}