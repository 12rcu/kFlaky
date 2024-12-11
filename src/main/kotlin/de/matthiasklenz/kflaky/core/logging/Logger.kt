package de.matthiasklenz.kflaky.core.logging

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun getLogger(name: String): Logger {
    return Logger(name)
}

class Logger (private val name: String) {
    private val file = File("debug.txt")

    init {
        if(!file.exists()) {
            file.createNewFile()
        }
    }

    fun logTestClass(fullName: String) {

    }

    fun info(vararg data: String) {
        data.forEach {
            writeLine("INFO", it)
        }
    }

    fun warn(vararg data: String) {
        data.forEach {
            writeLine("WARN", it)
        }
    }

    fun error(vararg data: String) {
        data.forEach {
            writeLine("ERR", it)
        }
    }

    private fun writeLine(level: String, message: String) {
        val today = Calendar.getInstance()
        val sendDateUAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(today.time)
        file.appendText("$sendDateUAT [$level] $name: $message")
    }
}