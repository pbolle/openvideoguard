import Dependencies._
import play.sbt.PlayImport._
import play.sbt.PlayScala
import play.sbt.routes.RoutesKeys._
import sbt.Keys._
import sbt._

//import play.sbt.PlayImport

object OpenVideoguardBuild extends Build {
  val organization = "org.openvideoguard"
  val version = "0.1-SNAPSHOT"
  val scalaVersion = "2.11.6"

  lazy val openvideoguard = Project("openvideoguard", file("."))
    .aggregate(web)
    .settings(
      run := {
        (run in web in Compile).evaluated
      }
    )

  lazy val core = Project(
    id = "core",
    base = file("core"),
    settings = Seq(
      name := "openvideoguard-core",
      resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
      libraryDependencies ++= Seq(akkaActor, ftpServer, slf4jApi, scrimage, baticTranscoder, playSlick, playSlickEvolutions)
    )
  )

  lazy val web = (project in file("web")).settings(
    name := "openvideoguard-web",
    routesGenerator := InjectedRoutesGenerator,
    routesImport += "org.openguard.core.models._",
    resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
    libraryDependencies ++= Seq(
      specs2 % Test,
      playSlick,
      playSlickEvolutions,
      h2,
      bootstrap
    )
  ).enablePlugins(PlayScala)
    .dependsOn(core)

}
