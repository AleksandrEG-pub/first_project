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
    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.testcontainers.all)
}

application {
    mainClass = "org.example.App"
}

tasks.named<Test>("test") {
    systemProperty("aspectj.disable", "true")
}
