package de.matthiasklenz.kflaky

import de.matthiasklenz.kflaky.adapters.commandline.handleCommandLineArgs
import de.matthiasklenz.kflaky.adapters.terminal.centerSring
import de.matthiasklenz.kflaky.adapters.terminal.createTerminal
import de.matthiasklenz.kflaky.core.strategy.tuscansq.TuscanGenerator
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking {
    val config = handleCommandLineArgs(args)
    val terminal = async {
        createTerminal()
    }

    TuscanGenerator().generate()

    terminal.await()    //wait for UI exit
    return@runBlocking
}
