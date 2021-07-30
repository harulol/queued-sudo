plugins {
    java
    kotlin("jvm") version "1.5.21"
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
    implementation("dev.hawu.plugins:HikariLibrary:0.5.0-beta")
    implementation("org.jetbrains:annotations:21.0.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.getByName<Jar>("jar") {
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
    
    val buildType = System.getenv("build")
    when(buildType?.toLowerCase()) {
        "dev" -> archiveFileName.set("${project.name}-$lastVersion-dev-b$lastBuild.jar")
        "pre", "prerelease" -> archiveFileName.set("${project.name}-$lastVersion-PRE.jar")
        "rel", "release", "production" -> archiveFileName.set("${project.name}-$lastVersion.jar")
        "snapshot" -> archiveFileName.set("${project.name}-$lastVersion-SNAPSHOT.jar")
        null -> println("Weird...")
        else -> throw IllegalArgumentException("Unrecognized build notice.")
    }
    
    buildFile.printWriter().apply {
        println(lastVersion)
        println(lastBuild)
        flush()
        close()
    }
}