name := """BackendWebService"""
organization := "com.nextlevelstudy"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  filters,
  evolutions,
  ws,
  ehcache,
  cacheApi,
  "com.typesafe.play" %% "play-json" % "2.7.4",
  "com.typesafe.play" %% "play-slick" % "4.0.2",
  "com.typesafe.play" %% "play-slick-evolutions" % "4.0.2",
  "com.h2database" % "h2" % "1.4.192",
  "org.julienrf" %% "play-json-derived-codecs" % "6.0.0",
  "org.mindrot" % "jbcrypt" % "0.4",
  "commons-validator" % "commons-validator" % "1.6",
  "org.apache.commons" % "commons-lang3" % "3.9",

  "org.scalaz" %% "scalaz-core" % "7.2.29",
  "com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided",
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.nextlevelstudy.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.nextlevelstudy.binders._"
