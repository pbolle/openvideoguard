import sbt._

object Dependencies {
  /** ************************/
  /** Compile dependencies **/
  /** ************************/
  val akkaActor = "com.typesafe.akka" % "akka-actor_2.10" % "2.3.11" withSources() withJavadoc()
  val ftpServer = "org.apache.ftpserver" % "ftpserver-core" % "1.0.6" withSources() withJavadoc()
  val slf4jApi = "org.slf4j" % "slf4j-api" % "1.7.12"
  val scrimage = "com.sksamuel.scrimage" % "scrimage-io_2.10" % "2.0.0" withSources()
  val baticTranscoder = "org.apache.xmlgraphics" % "batik-transcoder" % "1.8"
  val playSlick = "com.typesafe.play" %% "play-slick" % "1.0.0" withSources() withJavadoc()
  val playSlickEvolutions = "com.typesafe.play" %% "play-slick-evolutions" % "1.0.0" withSources() withJavadoc()
  val h2 = "com.h2database" % "h2" % "1.4.187"
  val bootstrap = "org.webjars" % "bootstrap" % "3.3.5"
  val jquery = "org.webjars" % "jquery" % "1.11.3"
}
