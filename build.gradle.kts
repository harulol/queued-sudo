plugins {
    scala
    java
}

group = "dev.hawu.plugins"
version = "1.0-SNAPSHOT"

setProperty("libsDirName", "QueuedSudo")

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
}

dependencies {
    implementation("org.scala-lang:scala3-library_3:3.1.2")
    implementation("org.bukkit:bukkit:1.8-R0.1-SNAPSHOT")
    implementation("dev.hawu.plugins:hikari-library:1.6-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    archiveBaseName.set("QueuedSudo")
}
