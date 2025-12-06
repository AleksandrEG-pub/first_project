plugins {
    application
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "org.example"
version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(project(":logging"))
    implementation(project(":audit"))
    implementation(project(":database-connector"))
    implementation(libs.bundles.spring.web)
    implementation(libs.bundles.validation)
    implementation(libs.liquibase.core)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    testAnnotationProcessor(libs.mapstruct.processor)


    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.testcontainers.all)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

application {
    mainClass = "org.example.App"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    systemProperty("aspectj.disable", "true")
}
