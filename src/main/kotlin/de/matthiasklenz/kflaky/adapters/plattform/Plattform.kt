package de.matthiasklenz.kflaky.adapters.plattform

import java.io.File

object Plattform {
    val lineSeperator = System.lineSeparator() ?: "\n"
    val pathSeperator: String = File.separator
}