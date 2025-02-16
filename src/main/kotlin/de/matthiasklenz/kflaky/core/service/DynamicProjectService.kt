package de.matthiasklenz.kflaky.core.service

import de.matthiasklenz.kflaky.adapters.github.GitHubRepoItem
import de.matthiasklenz.kflaky.adapters.project.ProjectConfigDto
import de.matthiasklenz.kflaky.core.strategy.TestExecutionStrategy
import java.io.File

class DynamicProjectService {
    fun detectConfig(projectDir: File, gitHubRepo: GitHubRepoItem): ProjectConfigDto? {
        val framework = "jUnit"
        var command = ""

        projectDir.walk().maxDepth(1).forEach { file ->
            if (file.isFile) {
                if (file.name == "gradlew") {
                    command = "./gradlew test"
                    return@forEach
                }
                if (file.name == "pom.xml") {
                    command = "/usr/share/maven/bin/mvn test -f pom.xml"
                    return@forEach
                }
            }
        }

        if (command == "")
            return null
        if (!gitHubRepo.language.equals("kotlin", ignoreCase = true) &&
            !gitHubRepo.language.equals("java", ignoreCase = true)
        ) {
            return null
        }

        return ProjectConfigDto(
            projectDir.name,
            framework,
            gitHubRepo.language,
            projectDir.absolutePath,
            command,
            "",
            "",
            "",
            TestExecutionStrategy.TUSCAN_SQUARES,
            10,
            true
        )
    }
}