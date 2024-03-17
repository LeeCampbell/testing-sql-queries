import java.io.StringWriter
import javax.sql.DataSource

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

fun getSimplePgDataSource(): DataSource {
    val source = org.postgresql.ds.PGSimpleDataSource()
    source.applicationName = "Testing SQL Queries"
    source.user = "postgres"
    source.password = "mysecretpassword"
    //source.setURL("jdbc:postgresql://testsql-postrges-server:5432/leecampbell") //if running from within docker compose
    source.setURL("jdbc:postgresql://localhost:5432/leecampbell")  //if running from local pointing to docker
    return source
}

fun executeSqlCommand(sql: String) {
    val datasource = getSimplePgDataSource()

    datasource.connection.use { connection ->
        val statement = connection.prepareStatement(sql)
        statement.execute()
    }
}