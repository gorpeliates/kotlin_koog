plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.spring") version "2.2.0"
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.samples.a2a"
version = "1.0-SNAPSHOT"

springBoot {
    mainClass.set("server.MASServerApplicationKt")
}
repositories {
    mavenCentral()
}

val a2aSdkVersion = "0.2.3.Beta1"
val openTelemetryVersion = "1.34.0"
val micrometerTracingVersion = "1.5.2"

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlinx" && requested.name.startsWith("kotlinx-coroutines")) {
            useVersion("1.10.2")
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))

    // Kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    //spring
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Dotenv
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")

    implementation("io.github.a2asdk:a2a-java-sdk-common:${a2aSdkVersion}")
    implementation("io.github.a2asdk:a2a-java-sdk-spec:${a2aSdkVersion}")
    implementation("io.github.a2asdk:a2a-java-sdk-client:${a2aSdkVersion}")
    implementation("io.github.a2asdk:a2a-java-sdk-server-common:${a2aSdkVersion}")

    // OpenTelemetry dependencies with forced version alignment
    implementation("io.opentelemetry:opentelemetry-sdk:${openTelemetryVersion}")
    implementation("io.opentelemetry:opentelemetry-sdk-trace:${openTelemetryVersion}")
    implementation("io.opentelemetry:opentelemetry-api:${openTelemetryVersion}")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp:${openTelemetryVersion}")

    // Koog dependencies
    implementation("ai.koog:koog-agents:0.2.1")
    implementation("ch.qos.logback:logback-classic:1.5.13")

    // Micrometer tracing bridge
    implementation("io.micrometer:micrometer-tracing-bridge-otel:${micrometerTracingVersion}")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}