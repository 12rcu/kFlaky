package de.matthiasklenz.kflaky.core.tasks

import java.nio.file.Path

interface CollectResults {
    fun collect(buildPath: Path)
}