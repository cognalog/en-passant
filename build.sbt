import org.scalajs.linker.interface.ModuleSplitStyle

lazy val commonSettings = Seq(
  version := "0.1",
  scalaVersion := "2.13.4",
  organization := "com.enpassant"
)

val AkkaVersion = "2.6.20"
val AkkaHttpVersion = "10.2.10"

lazy val backend = project
  .in(file("backend"))
  .settings(
    commonSettings,
    name := "en-passant-backend",
    libraryDependencies ++= Seq(
      "org.scalamock" %% "scalamock" % "4.4.0" % Test,
      "org.scalatest" %% "scalatest" % "3.1.0" % Test,
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
      "com.typesafe.play" %% "play-json" % "2.9.4"
    )
  )

lazy val frontend = project
  .in(file("frontend"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    commonSettings,
    name := "en-passant-frontend",
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.4.0",
      "com.raquo" %%% "laminar" % "15.0.1",
      "org.scalamock" %%% "scalamock" % "5.2.0" % Test,
      "org.scalatest" %%% "scalatest" % "3.2.15" % Test
    ),
    Compile / fastLinkJS / artifactPath := baseDirectory.value / "target" / "scala-2.13" / "en-passant-frontend-fastopt" / "main.js",
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(ModuleSplitStyle.FewestModules)
    }
  )

lazy val root = project
  .in(file("."))
  .aggregate(backend, frontend)
  .settings(
    name := "en-passant",
    publish := {},
    publishLocal := {}
  )
