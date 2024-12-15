package de.matthiasklenz.kflaky.adapters.terminal

import kotlin.text.StringBuilder

fun containLength(content: String, maxLength: Int): String {
    if (content.length <= maxLength)
        return content
    return content.dropLast((content.length - maxLength) + 3).plus("...")
}

/**
 * numbers are not 0 based - start counting at 1!
 * @param content content to center
 * @param within the space size to center in
 * @param spcaeCharacter the character to use when spacing, default space
 */
fun centerSring(content: String, within: Int, spcaeCharacter: Char = ' '): String {
    if(content.length >= within)
        return content
    val startContent = (within - content.length) / 2
    val builder = StringBuilder()
    for (i in 0 until within) builder.append(spcaeCharacter)
    builder.replace(startContent + 1, startContent + content.length + 1, content)
    return builder.toString()
}

fun centerAndContain(content: String, within: Int, spcaeCharacter: Char = ' '): String {
    val contained = containLength(content, within)
    return centerSring(contained, within, spcaeCharacter)
}

fun appendWhitespaceToString(content: String, size: Int): String {
    if(content.length >= size) {
        return content
    }
    val builder = StringBuilder(content)
    for (i in content.length until  size) {
        builder.append(" ")
    }
    return builder.toString()
}