val scala3Version = "3.3.3"

val gatlingExclude = Seq(
  ExclusionRule("com.typesafe.akka", "akka-actor_2.13"),
  ExclusionRule("org.scala-lang.modules", "scala-java8-compat_2.13"),
  ExclusionRule("com.typesafe.akka", "akka-slf4j_2.13")
)

val gatlingHigh = "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.11.3" % "test" excludeAll (gatlingExclude *)
val gatlingTest = "io.gatling" % "gatling-test-framework" % "3.11.3" % "test" excludeAll (gatlingExclude *)

lazy val gatlingDependencies = Seq(
  gatlingHigh,
  gatlingTest
)

lazy val commonSettings = Seq(
  version := "0.1.0-SNAPSHOT",
  scalaVersion := scala3Version,
  libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.10",
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test",
  libraryDependencies += "com.google.inject" % "guice" % "5.1.0",
  libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.1.0",
  libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.0-RC5",
  libraryDependencies += ("org.scala-lang.modules" %% "scala-swing" % "3.0.0").cross(CrossVersion.for3Use2_13),
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % "10.5.3",
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.3",
    "com.typesafe.akka" %% "akka-actor-typed" % "2.8.5",
    "com.typesafe.akka" %% "akka-stream" % "2.8.5"
  ),
  libraryDependencies ++= Seq(
    "com.typesafe.slick" %% "slick" % "3.5.0",
    //"org.slf4j" % "slf4j-nop" % "2.0.13",
    "ch.qos.logback" % "logback-classic" % "1.5.6",
    "org.postgresql" % "postgresql" % "42.7.3",
    ("org.mongodb.scala" %% "mongo-scala-driver" % "5.1.0")
      .cross(CrossVersion.for3Use2_13)
  ),
  libraryDependencies ++= gatlingDependencies,
  libraryDependencies ++= Seq(
    ("org.apache.kafka" %% "kafka-streams-scala" % "3.7.0").cross(CrossVersion.for3Use2_13),
    "org.apache.kafka" % "kafka-clients" % "3.7.0"
  ),
  jacocoReportSettings := JacocoReportSettings(
    "Jacoco Coverage Report",
    None,
    JacocoThresholds(),
    Seq(
      JacocoReportFormats.ScalaHTML,
      JacocoReportFormats.XML
    ), // note XML formatter
    "utf-8"
  ),
  jacocoCoverallsServiceName := "github-actions",
  jacocoCoverallsBranch := sys.env.get("CI_BRANCH"),
  jacocoCoverallsPullRequest := sys.env.get("GITHUB_EVENT_NAME"),
  jacocoCoverallsRepoToken := sys.env.get("COVERALLS_REPO_TOKEN"),
  Test / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.ScalaLibrary
)

lazy val gui = project
  .in(file("gui"))
  .settings(
    name := "gui",
    commonSettings,
    jacocoExcludes := Seq("*")
  )
  .dependsOn(core, model)

lazy val tui = project
  .in(file("tui"))
  .settings(
    name := "tui",
    commonSettings,
    jacocoExcludes := Seq("*")
  )
  .dependsOn(core, model)
  .enablePlugins(JacocoPlugin)

lazy val core = project
  .in(file("core"))
  .settings(
    name := "core",
    commonSettings
  )
  .dependsOn(model, persistence)
  .enablePlugins(JacocoPlugin)

lazy val model = project
  .in(file("model"))
  .settings(
    name := "model",
    commonSettings
  )
  .enablePlugins(JacocoPlugin)

lazy val persistence = project
  .in(file("persistence"))
  .settings(
    name := "persistence",
    commonSettings
  )
  .dependsOn(model)
  .enablePlugins(GatlingPlugin)

lazy val kafka = project
  .in(file("kafka"))
  .settings(
    name := "kafka",
    commonSettings
  )
  .enablePlugins(JacocoPlugin)

lazy val root = project
  .in(file("."))
  .settings(
    name := "Reversi",
    commonSettings
  )
  .enablePlugins(JacocoPlugin, JacocoCoverallsPlugin, GatlingPlugin)
  .aggregate(gui, tui, core, model, persistence, kafka)

