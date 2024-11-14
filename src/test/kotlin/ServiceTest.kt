import kotlin.test.Test
import kotlin.test.assertEquals

class ServiceTest {
    companion object {
        var service : Service? = null
    }

    @Test
    fun initService() {
        service = Service()
    }

    @Test
    fun testSum() {
        assertEquals(service!!.sum(1,2), 3)
    }
}