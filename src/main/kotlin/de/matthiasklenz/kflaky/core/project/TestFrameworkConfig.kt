package de.matthiasklenz.kflaky.core.project

interface TestFrameworkConfig {
    enum class Language {
        JAVA,
        KOTLIN
    }

    val frameworkUtil: FrameworkUtil?

    val language: Language
    val testAnnotation: Regex

    fun testSuiteStart(): Regex

    fun importStart(): Regex

    fun imports(): Set<String>

    /**
     * the ignore annotation
     */
    fun ignoreAnnotation(): String

    /**
     * @param index indeicates in which posotion the test is executed
     */
    fun testOrderAnnotation(index: Int): String

    /**
     * the annotation the test class gets when executing by order
     */
    fun classOrderAnnontaion(): String

    /**
     * identifies if a testsuite identifier matches a given test
     */
    fun isTestContentForTestSuite(testFileContent: String, testSuite: String): Boolean
}