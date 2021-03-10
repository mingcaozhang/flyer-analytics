lazy val root = Project("flyer-analytics-service", file(".")).settings(
  name := "flyer-analytics-service",
  scalafmtOnCompile := true,
  libraryDependencies ++= Dependencies.deps
)
