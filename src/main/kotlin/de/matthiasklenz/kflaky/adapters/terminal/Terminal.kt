package de.matthiasklenz.kflaky.adapters.terminal

import de.matthiasklenz.kflaky.core.project.ProjectProgress
import de.matthiasklenz.kflaky.core.project.ProjectState
import de.matthiasklenz.kflaky.core.project.TestProgress
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.fusesource.jansi.AnsiConsole
import org.jline.terminal.TerminalBuilder

object Appearance {
    const val BG = 0x707880
    const val SECONDARY = 0xb294bb
    const val PROGRESS = 0x5e8d87
    const val PRIMARY = 0xf0c674

    const val SUCCESS = 0xb5bd68
    const val ERR = 0xcc6666
    const val INFO = 0x81a2be
}

suspend fun createTerminal() = coroutineScope {
    val terminal = TerminalBuilder.builder().system(true).build()
    val width = terminal.width
    val height = terminal.height

    val render = ProjectRender().projectRender(width to height)
    render.await()

    AnsiConsole.systemUninstall()
}