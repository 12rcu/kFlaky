package de.matthiasklenz.kflaky.adapters.terminal

import de.matthiasklenz.kflaky.adapters.terminal.components.printFooter
import de.matthiasklenz.kflaky.adapters.terminal.components.printHeader
import de.matthiasklenz.kflaky.adapters.terminal.components.printMainConent
import de.matthiasklenz.kflaky.core.project.ProjectProgress
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.fusesource.jansi.Ansi.ansi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.qualifier
import org.ktorm.dsl.exists

class ProjectRender : KoinComponent {

    private val terminalLog: Channel<String> by inject(qualifier("terminal"))
    private val progressChannel: Channel<ProjectProgress> by inject(qualifier("progress"))
    var projectsProgress: MutableList<ProjectProgress> = mutableListOf()

    suspend fun projectRender(size: Pair<Int, Int>): Deferred<Unit> =
        coroutineScope {
            render(size, listOf(), listOf())
            val log: MutableList<String> = mutableListOf()

            return@coroutineScope async {
                val progressJob = launch {
                    for (p in progressChannel) {
                        val existing = projectsProgress.firstOrNull { it.name == p.name }
                        if (existing != null) {
                            existing.index = p.index
                            existing.testsToRun = p.testsToRun
                            existing.state = p.state
                        } else {
                            projectsProgress.add(p)
                        }
                        render(size, projectsProgress, log)
                    }
                }
                val logJob = launch {
                    for (l in terminalLog) {
                        log.add(l)
                        render(size, projectsProgress, log)
                    }
                }

                progressJob.join()
                logJob.join()
            }
        }

    private fun render(size: Pair<Int, Int>, porjects: List<ProjectProgress>, log: List<String>) {
        val (width, height) = size
        print(ansi().eraseScreen())
        printHeader(width, 20, 60)
        printMainConent(porjects, log, width, height - 5, 20, 60)
        printFooter(width, 20, 60)
    }
}

