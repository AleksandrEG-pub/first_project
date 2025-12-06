plugins {
    id("java-library")
    id("maven-publish")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "org.example_audit"
version = "1.0.0"

dependencies {
    implementation(project(":logging"))
    implementation(project(":database-connector"))
    implementation(libs.bundles.aspectj.all)
    implementation(libs.bundles.spring.web)
    implementation(libs.liquibase.core)
    implementation(libs.bundles.validation)
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    testAnnotationProcessor(libs.mapstruct.processor)
    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.testcontainers.all)
}
