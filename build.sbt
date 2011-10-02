// Project specific settings.
name := "XML Transform"

organization := "org.smop"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.1"

// Testing dependencies.
libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.6" % "test"
)

/////////////////////////////////////////////////////////////////////
// Anti-XML
/////////////////////////////////////////////////////////////////////
libraryDependencies += "com.codecommit" %% "anti-xml" % "0.3"

// resolvers ++= Seq(ScalaToolsSnapshots)

