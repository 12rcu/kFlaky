package de.matthiasklenz.kflaky.core.project

/**
 * @param testProgress indicates the current tests progression of each test suite and how often they need to be reran
 */
data class ProjectProgress(
    val name: String,
    val state: ProjectState,
    val testProgress: List<TestProgress>
)

enum class ProjectState {
    NOT_STARTED,
    SETUP,
    RUNNING,
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