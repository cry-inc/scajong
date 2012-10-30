name := "scajong"

version := "0.1"

scalaVersion := "2.9.2"

scalaSource in Compile := file("src/scajong/")

scalaSource in Test := file("test/scajong/")

libraryDependencies <+= scalaVersion { "org.scala-lang" % "scala-swing" % _ }