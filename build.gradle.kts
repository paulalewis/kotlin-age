plugins {
    kotlin("jvm") version "2.0.21"
}

group = "com.castlefrog.agl"
version = "1.0.2"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    testImplementation(kotlin("test-junit"))
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
