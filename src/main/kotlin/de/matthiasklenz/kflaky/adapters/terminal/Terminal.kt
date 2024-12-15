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
    const val BG = 0x662F4D
    const val SECONDARY = 0xAE445A
    const val PRIMARY = 0xF39F5A
}

suspend fun createTerminal() = coroutineScope {
    val terminal = TerminalBuilder.terminal()
    val width = terminal.width
    val height = terminal.height

    val render = ProjectRender().projectRender(width to height)
    render.await()

    AnsiConsole.systemUninstall()
}