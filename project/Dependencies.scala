import sbt._

object Dependencies {
  /** ************************/
  /** Compile dependencies **/
  /** ************************/
  val akkaActor = "com.typesafe.akka" % "akka-actor_2.11" % "2.3.11" withSources() withJavadoc()
  val ftpServer = "org.apache.ftpserver" % "ftpserver-core" % "1.0.6" withSources() withJavadoc()
  val slf4jApi = "org.slf4j" % "slf4j-api" % "1.7.12"
  val scrimage = "com.sksamuel.scrimage" % "scrimage-io_2.11" % "2.0.0-M2" withSources()
  val baticTranscoder = "org.apache.xmlgraphics" % "batik-transcoder" % "1.8"
}