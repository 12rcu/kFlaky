package de.matthiasklenz.kflaky.core.tasks.modify

import de.matthiasklenz.kflaky.core.plattform.Plattform

object TextModifyUtil {
    fun addFirstAnnotationBefore(content: String, regex: Regex, insertText: String): String {
        var replaced = false
        return regex.replace(content) {
            if(replaced)
                return@replace it.value
            val text = it.value
            val result = "${insertText}${Plattform.lineSeperator}${text}"
            replaced = true
            return@replace result
        }
    }
    fun addAnnotationBefore(content: String, regex: Regex, insertText: (matchNum: Int) -> String): String {
        var counter = 0
        return regex.replace(content) {
            val text = it.value
            val prefix = content.substring(0, it.range.first).split(Plattform.lineSeperator).last() //get the whitespace before the test
            val result = "${insertText(counter)}${Plattform.lineSeperator}" +
            "${prefix}${text}"
            counter ++
            return@replace result
        }
    }

    fun addImports(content: String, insertText: String, regex: Regex): String {
        if(regex.find(content) == null) {
            return insertText + Plattform.lineSeperator + content
        }
        var replaced = false
        return regex.replace(content) {
            if(replaced)
                return@replace it.value
            val result = "${it.value}${Plattform.lineSeperator}${insertText}${Plattform.lineSeperator}"
            replaced = true
            result
        }
    }
}