plugins {
    java
    id("maven-publish")
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    repositories {
        mavenLocal()
        mavenCentral()
    }
    afterEvaluate {
        dependencies {
            implementation(libs.spring.boot.autoconfigure)
            implementation(libs.spring.boot.starter.log4j2)
            modules {
                module("org.springframework.boot:spring-boot-starter-logging") {
                    replacedBy("org.springframework.boot:spring-boot-starter-log4j2", "Use Log4j2 instead of Logback")
                }
            }
            compileOnly(libs.lombok)
            annotationProcessor(libs.lombok)
            testCompileOnly(libs.lombok)
            testAnnotationProcessor(libs.lombok)
            implementation(libs.mapstruct)
            annotationProcessor(libs.mapstruct.processor)
            testAnnotationProcessor(libs.mapstruct.processor)
        }
    }
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }
    tasks.named<Test>("test") {
        useJUnitPlatform()
    }
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
            }
        }
        repositories {
            mavenLocal()
        }
    }
}