plugins {
    kotlin("jvm") version "2.0.20"
    `java-gradle-plugin`
}

gradlePlugin {
    plugins {
        create("kFlaky") {
            id = "de.matthiasklenz.kflaky.k-flaky"
            implementationClass = "de.matthiasklenz.kflaky.KFlaky"
        }
    }
}



group = "de.matthiasklenz"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-xml
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.18.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

