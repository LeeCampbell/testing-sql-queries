import org.approvaltests.Approvals
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class SampleApprovalTest {
    @Test
    fun testNormalJunit() {
        Assertions.assertEquals(5, 5)
    }

    @Test
    fun testWithApprovalTests() {
        Approvals.verify("Hello World")
    }
}