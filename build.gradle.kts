import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    kotlin("jvm") version "1.5.21"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

setProperty("libsDirName", name)
group = "dev.hawu.plugins"
version = "2.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.bukkit:bukkit:1.8-R0.1-SNAPSHOT")
    implementation("dev.hawu.plugins:HikariLibrary:0.5.1-beta")
    implementation("org.jetbrains:annotations:21.0.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

configurations.getByName("implementation").isCanBeResolved = true
var fileName: String? = null

fun computeFileName(): String {
    if(fileName != null) return fileName!!
    
    val buildFile = File("./build.txt")
    var lastVersion = ""
    var lastBuild = 0
    
    if(buildFile.exists()) {
        val lines = buildFile.readLines()
        if(lines.isNotEmpty()) {
            lastVersion = lines[0]
            lastBuild = lines[1].toInt()
        }
    } else buildFile.createNewFile()
    
    if(lastVersion != project.version) {
        lastBuild = 0
        lastVersion = project.version.toString()
    }
    
    lastBuild++
    
    val name: String = when(System.getenv("build")?.toLowerCase()) {
        "dev" -> "${project.name}-$lastVersion-dev-b$lastBuild"
        "pre", "prerelease" -> "${project.name}-$lastVersion-PRE"
        "rel", "release", "production" -> "${project.name}-$lastVersion"
        "snapshot" -> "${project.name}-$lastVersion-SNAPSHOT"
        null -> "${project.name}-$lastVersion-UNKNOWN"
        else -> throw IllegalArgumentException("Unrecognized build notice.")
    }
    
    buildFile.printWriter().apply {
        println(lastVersion)
        println(lastBuild)
        flush()
        close()
    }
    
    fileName = name
    return name
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.getByName<ShadowJar>("shadowJar") {
    minimize()
    exclude("META-INF/INDEX.LIST", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    configurations = listOf(project.configurations.getByName("implementation"))
    dependencies {
        include(dependency(dependencies.kotlin("stdlib")))
        include(dependency("dev.hawu.plugins:HikariLibrary:.*"))
        exclude(dependency("org.bukkit:bukkit:.*"))
        exclude(dependency("org.jetbrains:annotations:.*"))
    }
    relocate("dev.hawu.plugins.api", "dev.hawu.plugins.queuedsudo.libs.api")
    relocate("kotlin", "dev.hawu.plugins.queuedsudo.libs.kotlin")
    
    group = "shadow"
    description = "An uber jar that does not need any external dependencies."
    archiveFileName.set("${computeFileName()}-all.jar")
    conventionMapping.map("classifier") {
        "all"
    }
    
    manifest.inheritFrom(project.tasks.getByName<Jar>("jar").manifest)
}

tasks.create<ShadowJar>("no-lib") {
    minimize()
    exclude("META-INF/INDEX.LIST", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    configurations = listOf(project.configurations.getByName("implementation"))
    dependencies {
        exclude(dependency(dependencies.kotlin("stdlib")))
        include(dependency("dev.hawu.plugins:HikariLibrary:.*"))
        exclude(dependency("org.bukkit:bukkit:.*"))
        exclude(dependency("org.jetbrains:annotations:.*"))
    }
    relocate("dev.hawu.plugins.api", "dev.hawu.plugins.queuedsudo.libs.api")
    
    group = "shadow"
    description = "A jar that contains this project's classes and the library that requires Kotlin to run."
    archiveFileName.set("${computeFileName()}-noLib.jar")
    conventionMapping.map("classifier") {
        "all"
    }
    from(tasks.getByName<Jar>("jar").outputs)
    manifest.inheritFrom(project.tasks.getByName<Jar>("jar").manifest)
}

tasks.getByName<Jar>("jar") {
    ant.withGroovyBuilder {
        println("pp")
        "replace"(
            "file" to "src/main/resources/plugin.yml",
            "token" to "@VERSION@",
            "value" to computeFileName(),
        )
    }

    archiveFileName.set("${computeFileName()}.jar")
}

val fixup = tasks.create("fixup") {
    doLast {
        ant.withGroovyBuilder {
            "replace"(
                "file" to "src/main/resources/plugin.yml",
                "token" to computeFileName(),
                "value" to "@VERSION@",
            )
        }
    }
}

tasks.getByName("shadowJar").finalizedBy(fixup)