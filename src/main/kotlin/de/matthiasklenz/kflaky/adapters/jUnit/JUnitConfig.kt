package de.matthiasklenz.kflaky.adapters.jUnit

import de.matthiasklenz.kflaky.core.project.TestFramworkConfig

class JUnitConfig(override val lanaguage: TestFramworkConfig.Language) : TestFramworkConfig {
    override val testAnnotation: Regex = Regex("@Test")
    override val collectedImports: MutableSet<String> = mutableSetOf()

    override fun ignoreAnnotation (): String {
        collectedImports.add("import org.junit.jupiter.api.Disabled")
        return "@Disabled(\"kFlaky ignore\")"
    }

    override fun testOrderAnnotation(index: Int): String {
        collectedImports.add("import org.junit.jupiter.api.Order")
        return "@Order($index)"
    }

    override fun classOrderAnnontaion(): String {
        collectedImports.add("import org.junit.jupiter.api.MethodOrderer")
        collectedImports.add("import org.junit.jupiter.api.TestMethodOrder")
        return when (lanaguage) {
            TestFramworkConfig.Language.KOTLIN -> "@TestMethodOrder(MethodOrderer.OrderAnnotation::class)"
            TestFramworkConfig.Language.JAVA -> "@TestMethodOrder(MethodOrderer.OrderAnnotation.class)"
        }
    }
}