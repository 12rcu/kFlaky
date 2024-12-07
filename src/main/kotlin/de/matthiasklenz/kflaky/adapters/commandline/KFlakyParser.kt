package de.matthiasklenz.kflaky.adapters.commandline

import de.matthiasklenz.kflaky.core.strategy.TestExecutionStrategy
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import kotlinx.serialization.Serializable

fun handleCommandLineArgs(args: Array<String>): Config {
    val parser = ArgParser("kFlaky")

    val input by parser.option(ArgType.String, shortName = "p", description = "Project Path")
    val testExecutionOrder by parser.option(
        ArgType.Choice<TestExecutionStrategy>(
            choices = TestExecutionStrategy.entries,
            toVariant = { TestExecutionStrategy.valueOf(it) }
        ),
        shortName = "o",
        description = "Test Execution Strategy, default is Tuscan Square"
    ).default(TestExecutionStrategy.TUSCAN_SQUARES)
    val debug by parser.option(ArgType.Boolean, shortName = "d", description = "Turn on debug mode").default(false)

    parser.parse(args)
    return Config(
        input,
        testExecutionOrder,
        debug
    )
}

@Serializable
data class Config(
    val project: String?,
    val executionStrategy: TestExecutionStrategy,
    val debug: Boolean,
)