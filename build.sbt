ThisBuild / scalaVersion := "2.13.6"

val LogbackVersion = "1.2.5"
val Slf4jVersion = "1.7.32"
val AlpakkaVersion = "3.0.3"

val Slf4jApi = "org.slf4j" % "slf4j-api" % Slf4jVersion
val LogbackClassic = "ch.qos.logback" % "logback-classic" % LogbackVersion

// This brings everything along for the ride.
val Alpakka = "com.lightbend.akka" %% "akka-stream-alpakka-s3" % AlpakkaVersion

lazy val root: Project = project
  .in(file("."))
  .aggregate(common, good, bad)

lazy val common = project
  .in(file("common"))
  .settings(
    libraryDependencies ++= Seq(
      Slf4jApi,
      LogbackClassic,
      Alpakka
    )
  )

lazy val good: Project = project.in(file("good")).dependsOn(common)

lazy val bad: Project = project.in(file("bad")).dependsOn(common)
