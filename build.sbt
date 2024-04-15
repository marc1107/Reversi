val scala3Version = "3.3.3"

lazy val commonSettings = Seq(
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.10",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test",
    libraryDependencies += "com.google.inject" % "guice" % "5.1.0",
    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.1.0",
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.0-RC5",
    libraryDependencies += ("org.scala-lang.modules" %% "scala-swing" % "3.0.0").cross(CrossVersion.for3Use2_13),
    jacocoCoverallsServiceName := "github-actions",
    jacocoCoverallsBranch := sys.env.get("CI_BRANCH"),
    jacocoCoverallsPullRequest := sys.env.get("GITHUB_EVENT_NAME"),
    jacocoCoverallsRepoToken := sys.env.get("COVERALLS_REPO_TOKEN")
)

lazy val util = project
  .in(file("util"))
  .settings(
      name := "util",
      commonSettings
  )
  .dependsOn(model)

lazy val gui = project
  .in(file("gui"))
  .settings(
      name := "gui",
      commonSettings
  )
  .dependsOn(core, util)

lazy val tui = project
  .in(file("tui"))
  .settings(
      name := "tui",
      commonSettings
  )
  .dependsOn(core, util)
  .enablePlugins(JacocoPlugin)

lazy val core = project
  .in(file("core"))
  .settings(
      name := "core",
      commonSettings
  )
  .dependsOn(model, persistence, util)

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

lazy val root = project
  .in(file("."))
  .settings(
      name := "Reversi",
      commonSettings
  )
  .enablePlugins(JacocoCoverallsPlugin)
  .aggregate(gui, tui, core, util, model, persistence)