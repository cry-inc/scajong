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

libraryDependencies ++= Seq(
    "org.eclipse.jetty" % "jetty-server" % "8.1.7.v20120910" % "compile",
    "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "compile" artifacts Artifact("javax.servlet", "jar", "jar"))

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case "about.html"     => MergeStrategy.discard
    case x => old(x)
  }
}

resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                  "releases"  at "http://oss.sonatype.org/content/repositories/releases")