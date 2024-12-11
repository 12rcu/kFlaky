package de.matthiasklenz.kflaky.adapters.terminal

import de.matthiasklenz.kflaky.adapters.terminal.components.printFooter
import de.matthiasklenz.kflaky.adapters.terminal.components.printHeader
import de.matthiasklenz.kflaky.adapters.terminal.components.printMainConent
import de.matthiasklenz.kflaky.core.project.ProjectProgress
import de.matthiasklenz.kflaky.core.project.ProjectState
import de.matthiasklenz.kflaky.core.project.TestProgress
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.fusesource.jansi.Ansi.ansi
import org.fusesource.jansi.AnsiConsole
import org.jline.terminal.TerminalBuilder

object Apperance {
    const val BG = 0x662F4D
    const val SECONDARY = 0xAE445A
    const val PRIMARY = 0xF39F5A
}

suspend fun createTerminal() = coroutineScope {
    val terminal = TerminalBuilder.terminal()
    val width = terminal.width
    val height = terminal.height

    val mokPorjects = listOf(
        ProjectProgress(
            "test 1",
            ProjectState.DONE,
            listOf(
                TestProgress(
                    "do not display this!",
                    1f
                )
            )
        ),
        ProjectProgress(
            "test 2",
            ProjectState.RUNNING,
            listOf(
                TestProgress(
                    "display this!",
                    0.1f
                ),
                TestProgress(
                    "Test",
                    0.3f
                )
            )
        ),
        ProjectProgress(
            "test 3",
            ProjectState.NOT_STARTED,
            listOf(
                TestProgress(
                    "do not display this!",
                    0.1f
                )
            )
        )
    )

    AnsiConsole.systemInstall()

    val channel = Channel<List<ProjectProgress>>()

    launch {
        delay(1000)
        channel.send(mokPorjects)
        delay(5000)
        channel.close()
    }

    val render = projectRender(width to height, channel)
    render.await()

    AnsiConsole.systemUninstall()
}