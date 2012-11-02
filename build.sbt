import sbt._

import AssemblyKeys._

assemblySettings

mainClass in assembly := Some("scajong.ScaJong")

jarName in assembly := "scajong.jar"

target in assembly := file("./")

name := "scajong"

version := "0.1"

scalaVersion := "2.9.2"

scalaSource in Compile := file("src/scajong/")

scalaSource in Test := file("test/scajong/")

libraryDependencies <+= scalaVersion { "org.scala-lang" % "scala-swing" % _ }

libraryDependencies ++= Seq("junit" % "junit" % "4.8.2" % "test")

libraryDependencies ++= Seq("org.specs2" %% "specs2" % "1.12.2" % "test")
 
resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                  "releases"  at "http://oss.sonatype.org/content/repositories/releases")