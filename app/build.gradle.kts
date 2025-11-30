plugins {
    application
    id("com.gradleup.shadow") version "9.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.swagger.core.v3:swagger-annotations:2.2.3")
    implementation("io.swagger.core.v3:swagger-models:2.2.3")
    implementation("org.webjars:swagger-ui:4.18.2")

    implementation("org.apache.logging.log4j:log4j-core:2.24.2")
    implementation("org.apache.logging.log4j:log4j-api:2.24.2")

    implementation("org.springframework:spring-webmvc:6.2.14")
    implementation("org.springframework:spring-context:6.2.14")

    implementation("org.apache.tomcat.embed:tomcat-embed-core:11.0.14")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.2")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    implementation("org.mapstruct:mapstruct:1.6.3")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    implementation("org.aspectj:aspectjrt:1.9.25")
    implementation("org.aspectj:aspectjweaver:1.9.25")
    implementation("org.glassfish:jakarta.el:4.0.2")

    implementation("jakarta.validation:jakarta.validation-api:4.0.0-M1")
    implementation("org.hibernate.validator:hibernate-validator:9.1.0.Final")

    implementation("org.liquibase:liquibase-core:4.30.0")
    implementation("org.postgresql:postgresql:42.7.8")

    testImplementation("org.testcontainers:testcontainers:2.0.2")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter:2.0.2")
    testImplementation("org.testcontainers:postgresql:1.21.3")

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")

    testImplementation("org.assertj:assertj-core:4.0.0-M1")
    testImplementation("org.mockito:mockito-core:5.20.0")
    testImplementation("org.skyscreamer:jsonassert:1.5.3")
    implementation("com.jayway.jsonpath:json-path:2.10.0")
    testImplementation("org.hamcrest:hamcrest:3.0")
    testImplementation("org.springframework:spring-test:6.2.14")
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

tasks.shadowJar {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    mergeServiceFiles()
    manifest {
        attributes["Main-Class"] = "org.example.App"
    }
}
