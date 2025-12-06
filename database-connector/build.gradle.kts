plugins {
    id("java-library")
    id("maven-publish")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "org.example_database"
version = "1.0.0"

dependencies {
    implementation(libs.postgresql)
}
