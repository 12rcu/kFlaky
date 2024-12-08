import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.assertEquals

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ServiceTest {
    @Test
    @Order(1)
    fun testSum() {
        assertEquals( 1+ 2, 3)
    }
}