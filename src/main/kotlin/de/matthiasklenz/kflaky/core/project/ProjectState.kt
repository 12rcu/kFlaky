package de.matthiasklenz.kflaky.core.project

import java.util.concurrent.atomic.AtomicInteger

data class ProjectProgress(
    val name: String,
    var state: ProjectState,
    var testsToRun: Int,
    var index: AtomicInteger
)

enum class ProjectState {
    PRE_RUNS,
    NOT_STARTED,
    SETUP,
    OD_RUNS,
    CLASSIFICATION,
    CLEANUP,
    DONE;

    override fun toString(): String {
        return super.toString().lowercase().replace("_", " ")
    }
}

fun ProjectState.getShort(): String {
    return when(this) {
        ProjectState.PRE_RUNS -> "PR"
        ProjectState.OD_RUNS -> "RN"
        ProjectState.CLASSIFICATION -> "CV"
        ProjectState.CLEANUP -> "CL"
        ProjectState.DONE -> "DN"
        ProjectState.NOT_STARTED -> "NS"
        ProjectState.SETUP -> "ST"
    }
}

data class TestProgress(
    val name: String,
    val progress: Float
)