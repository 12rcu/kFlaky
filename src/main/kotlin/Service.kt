import com.fasterxml.jackson.dataformat.xml.XmlMapper
import de.matthiasklenz.kflaky.jUnit.TestCase
import de.matthiasklenz.kflaky.jUnit.TestSuite

class Service {
    fun sum(a: Int, b: Int): Int {
        return a + b
    }
}

fun main() {
    val data = TestSuite().apply {
        name = "test"
        tests = "2"
        skipped = "0"
        failures = "1"
        testcase = listOf(
            TestCase().apply {
                name = "test()"
                time = "0.1"
            }
        )
    }
    val mapper = XmlMapper()
    val dataStr = mapper.writeValueAsString(data)
    println(dataStr)
}