package de.matthiasklenz.kflaky.core.middleware

import org.slf4j.LoggerFactory

class KFlakyLogger {
    private val listeners = mutableListOf<(name: String, level: String, msg: String) -> Unit>()
    private val loggers = hashMapOf<String, MLogger>()

    fun get(name: String): Logger {
        return if(loggers.containsKey(name)) {
            loggers[name]!!
        } else {
            val l = MLogger(name)
            loggers[name] = l
            l
        }
    }

    fun registerListener(onMsg: (name: String, level: String, msg: String) -> Unit) {
        listeners.add(onMsg)
    }

    private fun send(name: String, level: String, msg: String) {
        listeners.forEach { it(name, level, msg) }
    }

    inner class MLogger(override val name: String) : Logger {
        private val logger = LoggerFactory.getLogger(name)

        override fun debug(msg: String) {
            logger.debug(msg)
            send(name, "DEBUG", msg)
        }

        override fun log(msg: String) {
            logger.info(msg)
            send(name, "LOG", msg)
        }

        override fun warn(msg: String) {
            logger.warn(msg)
            send(name, "WARN", msg)
        }

        override fun error(msg: String) {
            logger.error(msg)
            send(name, "ERROR", msg)
        }
    }
}

interface Logger {
    val name: String
    fun debug(msg: String)
    fun log(msg: String)
    fun warn(msg: String)
    fun error(msg: String)
}