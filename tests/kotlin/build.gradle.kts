plugins {
    kotlin("jvm") version "1.9.22"
}

group = "com.leecampbell"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("com.approvaltests:approvaltests:22.4.0")
    testImplementation("org.postgresql:postgresql:42.5.1")
    testImplementation("com.opencsv:opencsv:5.7.1")
}

tasks.test {
    useJUnitPlatform()
    setupTestLogging()
}
kotlin {
    jvmToolchain(19)
}

fun Test.setupTestLogging() {
    testLogging {
        events(
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
        )
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true

        addTestListener(object : TestListener {
            override fun beforeSuite(suite: TestDescriptor) {}
            override fun beforeTest(testDescriptor: TestDescriptor) {}
            override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
            override fun afterSuite(suite: TestDescriptor, result: TestResult) {
                if (suite.parent != null) { // will match the outermost suite
                    val output = "Results: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} passed, ${result.failedTestCount} failed, ${result.skippedTestCount} skipped)"
                    val startItem = "|  "
                    val endItem = "  |"
                    val repeatLength = startItem.length + output.length + endItem.length
                    val messages = """
                        ${(1..repeatLength).joinToString("") { "-" }}
                        $startItem$output$endItem
                        ${(1..repeatLength).joinToString("") { "-" }}
                    """.trimIndent()
                    println(messages)
                }
            }
        })
    }
}