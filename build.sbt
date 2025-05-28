name := "en-passant"

version := "0.1"

scalaVersion := "2.13.4"

val AkkaVersion = "2.6.20"
val AkkaHttpVersion = "10.2.10"

libraryDependencies ++= Seq(
  "org.scalamock" %% "scalamock" % "4.4.0" % Test,
  "org.scalatest" %% "scalatest" % "3.1.0" % Test,
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.typesafe.play" %% "play-json" % "2.9.4"
)
