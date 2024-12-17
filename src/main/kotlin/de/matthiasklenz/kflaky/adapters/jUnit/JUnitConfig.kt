package de.matthiasklenz.kflaky.adapters.jUnit

import de.matthiasklenz.kflaky.core.project.TestFramworkConfig

class JUnitConfig(override val lanaguage: TestFramworkConfig.Language) : TestFramworkConfig {
    override val testAnnotation: Regex = Regex("@Test(\\s|\\n|\\r\\n)")    //otherwise also match @TestMethodOrder we add later to the test

    override fun testSuiteStart(): Regex = Regex("(public |private |protected |internal )?class")

    override fun importStart(): Regex = Regex("package (.*)")

    override fun imports(): Set<String> {
        return when(lanaguage) {
            TestFramworkConfig.Language.KOTLIN -> setOf(
                "import org.junit.jupiter.api.Disabled",
                "import org.junit.jupiter.api.Order",
                "import org.junit.jupiter.api.MethodOrderer",
                "import org.junit.jupiter.api.TestMethodOrder"
            )
            TestFramworkConfig.Language.JAVA -> setOf(
                "import org.junit.jupiter.api.Disabled;",
                "import org.junit.jupiter.api.Order;",
                "import org.junit.jupiter.api.MethodOrderer;",
                "import org.junit.jupiter.api.TestMethodOrder;"
            )
        }
    }

    override fun ignoreAnnotation (): String {
        return "@Disabled(\"kFlaky ignore\")"
    }

    override fun testOrderAnnotation(index: Int): String {
        return "@Order($index)"
    }

    override fun classOrderAnnontaion(): String {
        return when (lanaguage) {
            TestFramworkConfig.Language.KOTLIN -> "@TestMethodOrder(MethodOrderer.OrderAnnotation::class)"
            TestFramworkConfig.Language.JAVA -> "@TestMethodOrder(MethodOrderer.OrderAnnotation.class)"
        }
    }

    override fun isTestContentForTestSuite(testFileContent: String, testSuite: String, test: String): Boolean {
        val testSuiteL = testSuite.split(".")
        val packageId = testSuiteL.dropLast(1).joinToString("\\.")
        val className = testSuiteL.last()

        val containsClass = testFileContent.contains(Regex("(public |private |protected |internal )?class $className"))
        val containsPackage = if(packageId == "") true else testFileContent.contains(Regex("package $packageId"))

        return containsClass && containsPackage
    }
}