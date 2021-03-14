lazy val root = (project in file("."))
  .aggregate(core, beacon, ingest)
  .settings(name := "flyer-analytics-service")

lazy val commonSettings =
  Seq(
    dockerBaseImage := "openjdk:8u212-jre-alpine",
    scalafmtOnCompile := true,
    libraryDependencies ++= Dependencies.deps
  )

lazy val core = (project in file("core"))
  .settings(name := "flyer-analytics-core", commonSettings)

lazy val beacon = (project in file("beacon"))
  .enablePlugins(AshScriptPlugin, JavaAppPackaging)
  .dependsOn(core)
  .settings(
    name := "flyer-analytics-beacon",
    packageName in Docker := "mingcaozhang/flyer-analytics-beacon",
    commonSettings
  )

lazy val ingest = (project in file("ingest"))
  .enablePlugins(AshScriptPlugin, JavaAppPackaging)
  .dependsOn(core)
  .settings(
    name := "flyer-analytics-ingest",
    packageName in Docker := "mingcaozhang/flyer-analytics-ingest",
    commonSettings
  )
