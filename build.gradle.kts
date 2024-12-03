plugins {
    kotlin("jvm") version "2.0.20"
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.2.1"
    //id("de.matthiasklenz.kflaky.kflaky")
}

gradlePlugin {
    website = "https://github.com/ysb33r/gradleTest"
    vcsUrl = "https://github.com/ysb33r/gradleTest.git"
    plugins {
        create("kFlaky") {
            id = "de.matthiasklenz.kflaky.kflaky"
            implementationClass = "de.matthiasklenz.kflaky.KFlaky"
        }
    }
}

publishing {
    repositories {
        maven {
            name = "localPluginRepository"
            url = uri("../local-plugin-repository")
        }
    }
}


group = "de.matthiasklenz"
version = "0.2"

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
    jvmToolchain(20)
}

