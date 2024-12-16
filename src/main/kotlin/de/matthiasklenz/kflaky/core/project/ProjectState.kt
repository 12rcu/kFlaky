package de.matthiasklenz.kflaky.core.project

data class ProjectProgress(
    val name: String,
    var state: ProjectState,
    var testsToRun: Int,
    var index: Int
)

enum class ProjectState {
    PRE_RUNS,
    NOT_STARTED,
    SETUP,
    OD_RUNS,
    EVAL,
    CLEANUP,
    DONE;

    override fun toString(): String {
        return super.toString().lowercase().replace("_", " ")
    }
}

data class TestProgress(
    val name: String,
    val progress: Float
)