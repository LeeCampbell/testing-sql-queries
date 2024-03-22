import org.approvaltests.namer.StackTraceNamer
import java.io.File

fun getInputFile(qualifier: String): File {
    val stackTraceNamer = StackTraceNamer()
    val sourcePath = stackTraceNamer.sourceFilePath
    val className = stackTraceNamer.info.className
    return File("$sourcePath/$className.$qualifier.input.txt")
}