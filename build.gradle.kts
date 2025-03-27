plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.20"

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
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.18.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

    // https://mvnrepository.com/artifact/io.insert-koin/koin-core-coroutines
    implementation("io.insert-koin:koin-core-coroutines:4.1.0-Beta5")
    
    //sql
    implementation("org.ktorm:ktorm-core:4.1.1")
    //https://mvnrepository.com/artifact/org.ktorm/ktorm-support-sqlite
    implementation("org.ktorm:ktorm-support-sqlite:4.1.1")
    //https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
    implementation("org.xerial:sqlite-jdbc:3.49.1.0")

    // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    implementation("ch.qos.logback:logback-classic:1.5.18")

    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(System.getenv("JVM_VERSION")?.toIntOrNull() ?: 11)
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
