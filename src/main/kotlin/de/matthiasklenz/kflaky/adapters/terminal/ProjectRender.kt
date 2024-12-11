package de.matthiasklenz.kflaky.adapters.terminal

import de.matthiasklenz.kflaky.adapters.terminal.components.printFooter
import de.matthiasklenz.kflaky.adapters.terminal.components.printHeader
import de.matthiasklenz.kflaky.adapters.terminal.components.printMainConent
import de.matthiasklenz.kflaky.core.project.ProjectProgress
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.fusesource.jansi.Ansi.ansi

suspend fun projectRender(size: Pair<Int, Int>, porjects: Channel<List<ProjectProgress>>): Deferred<Unit> =
    coroutineScope {
        render(size, listOf())
        return@coroutineScope async {
            for (p in porjects) {
                render(size, p)
            }
        }
    }

private fun render(size: Pair<Int, Int>, porjects: List<ProjectProgress>) {
    val (width, height) = size
    print(ansi().eraseScreen())
    printHeader(width, 20, 60)
    printMainConent(porjects, width, height - 5, 20, 60)
    printFooter(width, 20, 60)
}