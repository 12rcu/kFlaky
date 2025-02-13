package de.matthiasklenz.kflaky.core.tasks

import de.matthiasklenz.kflaky.adapters.plattform.Platform

object TextModifyUtil {
    fun addFirstAnnotationBefore(content: String, regex: Regex, insertText: String): String {
        var replaced = false
        return regex.replace(content) {
            if(replaced)
                return@replace it.value
            val text = it.value
            val result = "${insertText}${Platform.lineSeparator}${text}"
            replaced = true
            return@replace result
        }
    }
    fun addAnnotationBefore(content: String, regex: Regex, insertText: (matchNum: Int) -> String): String {
        var counter = 0
        return regex.replace(content) {
            val text = it.value
            val prefix = content.substring(0, it.range.first).split(Platform.lineSeparator).last() //get the whitespace before the test
            val result = "${insertText(counter)}${Platform.lineSeparator}" +
            "${prefix}${text}"
            counter ++
            return@replace result
        }
    }

    fun addImports(content: String, insertText: String, regex: Regex): String {
        if(regex.find(content) == null) {
            return insertText + Platform.lineSeparator + content
        }
        var replaced = false
        return regex.replace(content) {
            if(replaced)
                return@replace it.value
            val result = "${it.value}${Platform.lineSeparator}${insertText}${Platform.lineSeparator}"
            replaced = true
            result
        }
    }
}