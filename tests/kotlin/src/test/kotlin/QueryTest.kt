import org.approvaltests.Approvals
import org.junit.jupiter.api.Test

class QueryTest {

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

    private lateinit var database: Database

    @org.junit.jupiter.api.BeforeEach
    fun setUp() {
        val server: String = System.getenv("PG_SERVER") ?: "localhost"
        database = Database("postgres", "mysecretpassword", server)

        //Create test tables
        createTestPurchaseTable()
        createTestRefundTable()

        //Replace views to use test table
        substitutePurchaseView()
        substituteRefundView()
    }

    @org.junit.jupiter.api.AfterEach
    fun tearDown() {
        //Drop test only objects
        dropPurchaseView()
        dropRefundView()
        dropTestPurchaseTable()
        dropTestRefundTable()

        //Return schema to production target
        recreateSchema()
    }

    @Test
    fun purchases() {
        populateTestPurchaseTable(getInputFile("purchases").path)
        val result = database.executeQueryAsTsvResult(sql)
        Approvals.verify(result)
    }

    @Test
    fun refunds() {
        populateTestRefundTable(getInputFile("refunds").path)
        val result = database.executeQueryAsTsvResult(sql)
        Approvals.verify(result)
    }

    @Test
    fun purchasesAndRefunds() {
        populateTestPurchaseTable(getInputFile("purchases").path)
        populateTestRefundTable(getInputFile("refunds").path)
        val result = database.executeQueryAsTsvResult(sql)
        Approvals.verify(result)
    }

    @Test
    fun noData() {
        val result = database.executeQueryAsTsvResult(sql)

        Approvals.verify(result)
    }

    private fun createTestPurchaseTable() {
        val sql = "CREATE TABLE marketing_internal.purchase (\n" +
                "amount         DECIMAL(38,2)    NOT NULL, \n" +
                "status         sales_published.purchase_status  NOT NULL, \n" +
                "updated_at     TIMESTAMP        NOT NULL \n" +
                ");\n"
        database.executeSqlCommand(sql)
    }

    private fun dropTestPurchaseTable() {
        val sql = "DROP TABLE marketing_internal.purchase;"
        database.executeSqlCommand(sql)
    }

    private fun createTestRefundTable() {
        val sql = "CREATE TABLE marketing_internal.refund (\n" +
                "amount         DECIMAL(38,2)    NOT NULL, \n" +
                "status         sales_published.refund_status  NOT NULL, \n" +
                "updated_at     TIMESTAMP        NOT NULL \n" +
                ");\n"
        database.executeSqlCommand(sql)
    }

    private fun dropTestRefundTable() {
        val sql = "DROP TABLE marketing_internal.refund;"
        database.executeSqlCommand(sql)
    }

    private fun populateTestPurchaseTable(inputFileName: String) {
        val sql =
            "INSERT INTO marketing_internal.purchase(\n" +
                    "  amount,\n" +
                    "  status, \n" +
                    "  updated_at\n" +
                    ")\n" +
                    "VALUES  (?,?::sales_published.purchase_status,?);"
        database.insertData(inputFileName, sql)
    }
    private fun populateTestRefundTable(inputFileName: String) {
        val sql = """
            INSERT INTO marketing_internal.refund(
              amount,
              status, 
              updated_at
            )
            VALUES  (?,?::sales_published.refund_status,?);""".trimIndent()

        database.insertData(inputFileName, sql)
    }

    private fun substitutePurchaseView() {
        val sql = "CREATE OR REPLACE VIEW marketing_internal.vw_purchase \n" +
                "AS\n" +
                "SELECT amount, status, updated_at \n" +
                "FROM marketing_internal.purchase;\n"
        database.executeSqlCommand(sql)
    }

    private fun dropPurchaseView() {
        val sql = "DROP VIEW marketing_internal.vw_purchase;"
        database.executeSqlCommand(sql)
    }

    private fun substituteRefundView() {
        val sql = "CREATE OR REPLACE VIEW marketing_internal.vw_refund \n" +
                "AS\n" +
                "SELECT amount, status, updated_at \n" +
                "FROM marketing_internal.refund;\n"
        database.executeSqlCommand(sql)
    }

    private fun dropRefundView() {
        val sql = "DROP VIEW marketing_internal.vw_refund;"
        database.executeSqlCommand(sql)
    }

    private fun recreateSchema() {
        database.runFlywayMigration()
    }
}

