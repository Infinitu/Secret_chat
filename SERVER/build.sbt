name := "SERVER"

version := "1.0"

scalaVersion := "2.11.6"

unmanagedBase := baseDirectory.value / "libs"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.9",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.9",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.9",
  "com.typesafe.akka" %% "akka-stream-experimental" % "1.0-RC3",
  "com.typesafe.play" %% "play-json" % "2.4.0-RC3",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % "test",
  "org.mongodb" %% "casbah" % "2.8.0",
  "com.github.simplyscala" %% "scalatest-embedmongo" % "0.2.2" % "test",
  "net.debasishg" %% "redisclient" % "2.15",
  "com.github.kstyrc"%"embedded-redis"%"0.6",
  "log4j" % "log4j" % "1.2.17"
)

