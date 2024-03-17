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
}
kotlin {
    jvmToolchain(19)
}