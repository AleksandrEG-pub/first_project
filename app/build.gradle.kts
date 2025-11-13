plugins {
    application
    id("com.gradleup.shadow") version "9.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.liquibase:liquibase-core:4.30.0")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    mainClass = "org.example.App"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.shadowJar {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    mergeServiceFiles()
    manifest {
        attributes["Main-Class"] = "org.example.App"
    }
}
