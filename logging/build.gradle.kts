plugins {
    id("java-library")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "org.example_logging"
version = "1.0.0"

dependencies {
    implementation(libs.bundles.aspectj.all)
}
