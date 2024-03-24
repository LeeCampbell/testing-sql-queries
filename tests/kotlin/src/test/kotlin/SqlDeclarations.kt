import org.flywaydb.core.Flyway
import java.io.FileReader
import java.io.StringWriter
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.sql.DataSource

class Database(private val user: String, private val password: String, private val server: String) {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun executeQueryAsTsvResult(sql: String): String {
        val dataSource = getSimplePgDataSource()
        val stringWriter = StringWriter()
        dataSource.connection.use { connection ->
            val statement = connection.prepareStatement(sql)
            statement.executeQuery().use { resultSet ->
                writeResult(stringWriter, resultSet)
            }
        }
        return stringWriter.toString()
    }

    fun executeSqlCommand(sql: String) {
        val datasource = getSimplePgDataSource()

        datasource.connection.use { connection ->
            val statement = connection.prepareStatement(sql)
            statement.execute()
        }
    }

    fun insertData(inputFileName: String, sql: String) {
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

    fun runFlywayMigration() {
        val flyway = Flyway.configure()
            .dataSource(getSimplePgDataSource())
            .schemas("marketing_internal")
            .cleanDisabled(false)
            .load()

        flyway.migrate()
    }

    private fun getSimplePgDataSource(): DataSource {
        val source = org.postgresql.ds.PGSimpleDataSource()
        source.applicationName = "Testing SQL Queries"
        source.user = user
        source.password = password
        source.setURL("jdbc:postgresql://$server:5432/leecampbell")
        //source.setURL("jdbc:postgresql://testsql-postrges-server:5432/leecampbell") //if running from within docker compose
        //source.setURL("jdbc:postgresql://localhost:5432/leecampbell")  //if running from local pointing to docker.
        // If running locally, this will also require copying ../../flyway/marketing/sql to ./db/migrations in the class path.
        return source
    }

}