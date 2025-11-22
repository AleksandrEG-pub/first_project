plugins {
    application
    id("com.gradleup.shadow") version "9.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.tomcat.embed:tomcat-embed-core:11.0.14")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.1")

    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    implementation("org.mapstruct:mapstruct:1.6.3")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    implementation("org.liquibase:liquibase-core:4.30.0")
    implementation("org.postgresql:postgresql:42.7.8")

    testImplementation("org.testcontainers:testcontainers:2.0.2")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter:2.0.2")
    testImplementation("org.testcontainers:postgresql:1.21.3")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")

    testImplementation("org.assertj:assertj-core:4.0.0-M1")
    testImplementation("org.mockito:mockito-core:5.20.0")
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
