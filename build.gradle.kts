plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"

    application
}

group = "de.matthiasklenz"
version = "0.2"

repositories {
    mavenCentral()
}

application {
    mainClass.set("de.matthiasklenz.kflaky.KFlakyKt")
}

dependencies {
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-xml
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.18.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.fusesource.jansi:jansi:2.4.1")
    implementation("org.jline:jline:3.26.3")

    // https://mvnrepository.com/artifact/io.insert-koin/koin-core-coroutines
    implementation("io.insert-koin:koin-core-coroutines:4.1.0-Beta1")
    
    //sql
    implementation("org.ktorm:ktorm-core:4.1.1")
    implementation("org.ktorm:ktorm-support-sqlite:4.1.1")
    implementation("org.xerial:sqlite-jdbc:3.47.1.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(System.getenv("JVM_VERSION")?.toIntOrNull() ?: 21)
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "de.matthiasklenz.kflaky.KFlakyKt"
    }
    // To avoid the duplicate handling strategy error
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // To add all the dependencies
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}
