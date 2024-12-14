import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ServiceTest {
    companion object {
        var state = 0
    }

    @Test
    fun testSum() {
        assertEquals( 1+ 2, 3)
    }

    @Test
    fun flaky() {
        assertEquals(state, 0)
    }

    @Test
    fun polluter() {
        state = 2
    }
}