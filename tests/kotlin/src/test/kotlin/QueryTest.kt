import org.approvaltests.Approvals
import org.junit.jupiter.api.Test
import java.io.FileReader
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class QueryTest {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    //Once we are happy with our query, it could be promoted to a view in the `marketing_published` schema.
    private val sql = """
WITH cashflows as(
    SELECT
        updated_at,
        CASE WHEN status = 'Settled' THEN amount END as CashIn,
        CASE WHEN status = 'Cancelled' THEN amount END as CashOut
    FROM
        marketing_internal.vw_Purchase
    UNION ALL
    SELECT
        updated_at,
        0 as CashIn,
        CASE WHEN status IN ('Refunded', 'Returned') THEN amount END as CashOut
    FROM
        marketing_internal.vw_Refund
)

SELECT
    month_name_abbreviated || ' ' || year_actual as Month,
    COALESCE(SUM(CashIn), 0) as CashIn,
    COALESCE(SUM(CashOut), 0) as CashOut
FROM
    marketing_internal.vw_date d
    LEFT OUTER JOIN cashflows c
        ON d.date_actual = c.updated_at::Date
WHERE
   '2000-01-01' <= date_actual AND date_actual < '2000-02-01'
GROUP BY
    Month;
    """.trimIndent()


    @org.junit.jupiter.api.BeforeEach
    fun setUp() {
        //Create test tables
        createTestPurchaseTable()
        createTestRefundTable()

        //Replace views to use test table
        substitutePurchaseView()
        substituteRefundView()
    }

    @org.junit.jupiter.api.AfterEach
    fun tearDown() {
        //Replace view to point to real table
        reinstatePurchaseView()
        reinstateRefundView()

        //Drop table
        dropTestPurchaseTable()
        dropTestRefundTable()
    }

    @Test
    fun purchases() {
        populateTestPurchaseTable(getInputFile("purchases").path)
        val result = executeQueryAsTsvResult(sql)
        Approvals.verify(result)
    }

    @Test
    fun refunds() {
        populateTestRefundTable(getInputFile("refunds").path)
        val result = executeQueryAsTsvResult(sql)
        Approvals.verify(result)
    }

    @Test
    fun purchasesAndRefunds() {
        populateTestPurchaseTable(getInputFile("purchases").path)
        populateTestRefundTable(getInputFile("refunds").path)
        val result = executeQueryAsTsvResult(sql)
        Approvals.verify(result)
    }

    @Test
    fun noData() {
        val result = executeQueryAsTsvResult(sql)

        Approvals.verify(result)
    }

    private fun createTestPurchaseTable() {
        val sql = "CREATE TABLE marketing_internal.purchase (\n" +
                "amount         DECIMAL(38,2)    NOT NULL, \n" +
                "status         sales_published.purchase_status  NOT NULL, \n" +
                "updated_at     TIMESTAMP        NOT NULL \n" +
                ");\n"
        executeSqlCommand(sql)
    }

    private fun dropTestPurchaseTable() {
        val sql = "DROP TABLE marketing_internal.purchase;"
        executeSqlCommand(sql)
    }

    private fun createTestRefundTable() {
        val sql = "CREATE TABLE marketing_internal.refund (\n" +
                "amount         DECIMAL(38,2)    NOT NULL, \n" +
                "status         sales_published.refund_status  NOT NULL, \n" +
                "updated_at     TIMESTAMP        NOT NULL \n" +
                ");\n"
        executeSqlCommand(sql)
    }

    private fun dropTestRefundTable() {
        val sql = "DROP TABLE marketing_internal.refund;"
        executeSqlCommand(sql)
    }

    private fun populateTestPurchaseTable(inputFileName: String) {
        val sql =
            "INSERT INTO marketing_internal.purchase(\n" +
                    "  amount,\n" +
                    "  status, \n" +
                    "  updated_at\n" +
                    ")\n" +
                    "VALUES  (?,?::sales_published.purchase_status,?);"
        insertData(inputFileName, sql)
    }
    private fun populateTestRefundTable(inputFileName: String) {
        val sql = """
            INSERT INTO marketing_internal.refund(
              amount,
              status, 
              updated_at
            )
            VALUES  (?,?::sales_published.refund_status,?);""".trimIndent()

        insertData(inputFileName, sql)
    }

    private fun insertData(inputFileName: String, sql: String) {
        val datasource = getSimplePgDataSource()
        FileReader(inputFileName).use { fileReader ->
            val reader = tsvReader(fileReader)

            datasource.connection.use { connection ->
                connection.autoCommit = false
                val statement = connection.prepareStatement(sql)
                reader.forEach { row ->
                    val amount = BigDecimal(row[0])
                    val status = row[1]
                    val updatedAt = LocalDateTime.parse(row[2], formatter)

                    statement.setObject(1, amount)
                    statement.setObject(2, status)
                    statement.setObject(3, updatedAt)
                    statement.addBatch()
                }
                statement.executeBatch()
                connection.commit()
            }
        }
    }

    private fun substitutePurchaseView() {
        val sql = "CREATE OR REPLACE VIEW marketing_internal.vw_purchase \n" +
                "AS\n" +
                "SELECT amount, status, updated_at \n" +
                "FROM marketing_internal.purchase;\n"
        executeSqlCommand(sql)
    }

    private fun reinstatePurchaseView() {
        //OR use Flyway repeatable migration to repair the state back to the correct view.
        val sql = "CREATE OR REPLACE VIEW marketing_internal.vw_purchase \n" +
                "AS\n" +
                "SELECT amount, status, updated_at \n" +
                "FROM sales_published.purchase;\n"
        executeSqlCommand(sql)
    }

    private fun substituteRefundView() {
        val sql = "CREATE OR REPLACE VIEW marketing_internal.vw_refund \n" +
                "AS\n" +
                "SELECT amount, status, updated_at \n" +
                "FROM marketing_internal.refund;\n"
        executeSqlCommand(sql)
    }

    private fun reinstateRefundView() {
        //OR use Flyway repeatable migration to repair the state back to the correct view.
        val sql = "CREATE OR REPLACE VIEW marketing_internal.vw_refund \n" +
                "AS\n" +
                "SELECT amount, status, updated_at \n" +
                "FROM sales_published.refund;\n"
        executeSqlCommand(sql)
    }
}

