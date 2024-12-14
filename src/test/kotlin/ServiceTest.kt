import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ServiceTest {
    companion object {
        var state = 0
    }

    @Order(3)
    @Test
    fun testSum() {
        assertEquals( 1+ 2, 3)
    }

    @Order(2)
    @Test
    fun flaky() {
        assertEquals(state, 0)
    }

    @Order(1)
    @Test
    fun polluter() {
        state = 2
    }
}