plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.serialization") version "1.9.24"
    id("io.qameta.allure") version "2.11.2"
}

group = "com.bhft"
version = "0.0.1"

val ktorVersion: String by project
val kotlinVersion: String by project
val junitVersion: String by project
val allureVersion: String by project

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-resources:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-auth:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")

    implementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    implementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    implementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    implementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    implementation("io.qameta.allure:allure-junit5:$allureVersion")
    implementation("io.qameta.allure:allure-okhttp3:$allureVersion")

    implementation("io.strikt:strikt-core:0.34.1")

    implementation("io.github.microutils:kotlin-logging:2.1.21")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.2")
    implementation("io.ktor:ktor-client-okhttp-jvm:2.3.8")

    testImplementation(kotlin("test", kotlinVersion))
    testImplementation(kotlin("test-junit5", kotlinVersion))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
//    testImplementation("org.junit.platform:junit-platform-suite-engine:1.10.2")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
