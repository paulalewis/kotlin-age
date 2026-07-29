import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    kotlin("jvm") version "2.0.21"
}

group = "com.castlefrog.agl"
version = "0.3.1"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(platform("io.arrow-kt:arrow-stack:1.2.4"))
    implementation("io.arrow-kt:arrow-core")

    testImplementation(kotlin("test-junit"))
    testImplementation("com.google.truth:truth:1.4.4")
}

tasks.test {
    useJUnit()
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Specification-Title" to "Abstract Game Engine",
                "Specification-Version" to archiveVersion,
                "Implementation-Title" to "com.castlefrog.agl",
                "Implementation-Version" to archiveVersion
            )
        )
    }
}
