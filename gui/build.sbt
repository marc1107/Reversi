name := "GuiServer"

version := "0.1"

val scala3Version = "3.3.3"


// Abhängigkeiten

lazy val dependencies = Seq(
  libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "2.1.1",
  libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.18",
  libraryDependencies += ("com.typesafe.play" %% "play-json" % "2.10.4"),
  libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % "2.8.5",
  libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.5.3",
  libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.8.5",
  libraryDependencies += "org.apache.cassandra" % "cassandra-all" % "4.1.4" excludeAll(
    ExclusionRule(organization = "org.slf4j", name = "slf4j-log4j12"),
    ExclusionRule(organization = "log4j", name = "log4j")
  ),libraryDependencies += ("com.typesafe.play" %% "play-json" % "2.10.4"),
)

// Ermöglicht das Erstellen eines "fetten" JARs, das alle Abhängigkeiten enthält
//assemblyMergeStrategy in assembly := {
  //case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  //case x => MergeStrategy.first
//}