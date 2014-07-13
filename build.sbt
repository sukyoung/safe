import de.johoop.ant4sbt.Ant4Sbt._

name := "JSAF"

version := "1.0"

scalaVersion := "2.9.2"

libraryDependencies += "junit" % "junit" % "4.11" % "test"

parallelExecution in Test := false

antSettings
