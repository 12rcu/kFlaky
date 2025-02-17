package de.matthiasklenz.kflaky.core.service

import de.matthiasklenz.kflaky.adapters.persistence.SqlLiteDB
import de.matthiasklenz.kflaky.core.project.ProjectInfo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class OdMinimizerService : KoinComponent {
    private val sqlLiteDB: SqlLiteDB by inject()

    fun addMinimizedTestCases(projectInfo: ProjectInfo) {

    }

    private fun getIndexOfTest() {

    }

    private fun minimizeFailure() {

    }

    private fun minimizePass() {

    }
}