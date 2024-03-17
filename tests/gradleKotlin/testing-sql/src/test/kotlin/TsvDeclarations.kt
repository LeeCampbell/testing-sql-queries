import com.opencsv.*
import java.io.FileReader
import java.io.StringWriter
import java.sql.ResultSet
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

internal fun tsvReader(fileReader: FileReader): CSVReader {
    val parser = CSVParserBuilder()
        .withSeparator('\t')
        .build()
    val reader = CSVReaderBuilder(fileReader)
        .withCSVParser(parser)
        .withSkipLines(1)
        .build()
    return reader
}

internal fun writeResult(stringWriter: StringWriter, resultSet: ResultSet?) {
    CSVWriterBuilder(stringWriter)
        .withSeparator('\t')
        .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
        .withResultSetHelper(UTCResultSetHelper())
        .build().use { writer ->
            writer.writeAll(resultSet, true)
        }
}

class UTCResultSetHelper : ResultSetHelperService() {
    init {
        setDateTimeFormat("yyyy-MM-dd HH:mm:ssZ")
    }

    override fun handleTimestamp(timestamp: Timestamp?, timestampFormatString: String?): String? {
        val timeFormat = SimpleDateFormat(timestampFormatString)
        timeFormat.timeZone = TimeZone.getTimeZone("UTC")
        return if (timestamp == null) null else timeFormat.format(timestamp)
    }
}