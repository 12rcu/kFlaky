package de.matthiasklenz.kflaky.adapters.jUnit

import de.matthiasklenz.kflaky.core.exceptions.InvalidTestFileException
import de.matthiasklenz.kflaky.core.project.FrameWorkTestSuiteData
import de.matthiasklenz.kflaky.core.project.FrameworkTestFileContent
import de.matthiasklenz.kflaky.core.project.FrameworkUtil
import kotlin.text.Regex

class JUnitUtil : FrameworkUtil {
    override fun getTesFileInfo(
        fileName: String,
        content: String
    ): Result<FrameworkTestFileContent> {
        val classNameRegex: Regex = when (fileName.split(".").last()) {
            "kt",
            "java" -> Regex("(public |private |protected |internal |static )?class [a-zA-Z]+( )?(\\n)?( )*(\\t)*(extends|implements|:|\\{)?")

            else -> return Result.failure(InvalidTestFileException("Invalid file name $fileName."))
        }
        val methodNameRegex = when (fileName.split(".").last()) {
            "kt" -> Regex("@Test( )*(\\n)?( )*(\\t)*fun [a-zA-Z]*\\(")    //todo this only works if the test fun start directly after the @Test annotation
            "java" -> Regex("@Test( )*(\\n)?( )*(\\t)*public ([a-zA-Z])+ [a-zA-Z]+\\(") //todo this only works if the test method start directly after the @Test annotation
            else -> return Result.failure(InvalidTestFileException("Invalid file name $fileName."))
        }

        val classes = hashMapOf<Int, FrameWorkTestSuiteData>()

        val classIndexes = classNameRegex.findAll(content).mapIndexed { index, match ->
            index to match
                .value
                .split(" ")                                 //split on keywords
                .filterNot { it.isEmpty() || it == "{" || it == "extends" || it == "implements" }    // remove whitespace and the trailing {
                .last()                                     // the regex ends with { and we removed it
                .removeSuffix("{")                          // if there was no space between the class name and the {

        }.toMap()

        content.split(classNameRegex).drop(1).mapIndexed { index, content ->
            val methodNames = hashMapOf<Int, String>()
            val className = classIndexes[index]!!
            if (content.contains("@Test")) {
                methodNameRegex.findAll(content).forEachIndexed { methodIndex, methodMatch ->
                    val name = methodMatch
                        .value
                        .split(" ")                                 // split on method keywords
                        .filterNot { it.isEmpty() || it == "(" }    // remove whitespace and the trailing (
                        .last()                                     // the regex ends with ( and we removed it
                        .removeSuffix("(")                          // if there was no space between the method name and the (

                    methodNames[methodIndex] = name
                }
            }
            if (methodNames.isNotEmpty()) {
                classes[index] = FrameWorkTestSuiteData(className, methodNames)
            }
        }

        return Result.success(FrameworkTestFileContent(classes))
    }
}