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
    implementation(project(":database"))
    implementation(project(":web-common"))
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

tasks.register<Copy>("collectModuleConfigs") {
    val otherModules = rootProject.subprojects.filter { it.name != project.name }
    otherModules.forEach { otherModule ->
        val configFile = file("${otherModule.projectDir}/src/main/resources/application.yaml")
        if (configFile.exists()) {
            from(configFile) {
                rename("application.yaml", "application-${otherModule.name}.yaml")
            }
            println("Module1: Collecting from ${otherModule.name}")
        }
    }
    into("src/main/resources/modules")
}

tasks.processResources {
    dependsOn("collectModuleConfigs")
}
