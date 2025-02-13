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
