ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.1.2-RC1"

lazy val root = (project in file("."))
   .settings(
      name := "QueuedSudo",
      resolvers += Resolver.mavenLocal,
      libraryDependencies ++= Seq(
         "dev.hawu.plugins" % "hikari-library" % "1.5-SNAPSHOT",
         "org.bukkit" % "bukkit" % "1.8-R0.1-SNAPSHOT",
      ),
      artifactName := { (_: ScalaVersion, module: ModuleID, _: Artifact) =>
         s"QueuedSudo-${module.revision}.jar"
      },
   )
