plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "first_project"
include("app")
include("audit")
include("logging")
include("database")
