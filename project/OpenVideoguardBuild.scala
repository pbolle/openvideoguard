import sbt._
import sbt.Keys._
import Dependencies._

object OpenVideoguardBuild extends Build {

  lazy val core = Project(
    id = "core",
    base = file("core"),
    settings = Seq(
      name := "openvideoguard-core",
      organization := "org.openvideoguard",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.11.6",
      resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
      libraryDependencies ++= Seq(akkaActor,ftpServer,slf4jApi,scrimage,baticTranscoder)
   )
  )
  mainClass in (Compile, run) := Some("org.openguard.core.ImageFtplet")
}
