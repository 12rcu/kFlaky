package de.matthiasklenz.kflaky.adapters.jUnit

import de.matthiasklenz.kflaky.core.project.TestFramworkConfig

class JUnitConfig(override val lanaguage: TestFramworkConfig.Language) : TestFramworkConfig {
    override val testAnnotation: Regex = Regex("@Test(\\s|\\n|\\r\\n)")    //otherwise also match @TestMethodOrder we add later to the test
    override val imports: MutableSet<String> = mutableSetOf(
        "import org.junit.jupiter.api.Disabled",
        "import org.junit.jupiter.api.Order",
        "import org.junit.jupiter.api.MethodOrderer",
        "import org.junit.jupiter.api.TestMethodOrder"
    )

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
}