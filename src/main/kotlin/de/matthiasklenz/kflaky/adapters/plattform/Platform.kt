package de.matthiasklenz.kflaky.adapters.plattform

import java.io.File

object Platform {
    val lineSeparator = System.lineSeparator() ?: "\n"
    val pathSeparator: String = File.separator
}