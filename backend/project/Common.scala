package payroll
import sbt._
import Keys.{scalaVersion, _}
import play.sbt.PlayImport.{cache, ws, filters}

object Common {
  val dependencies = Seq(
    filters,
    cache,
    ws,
    "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
    "com.typesafe.play" %% "play-slick" % "2.0.0",
    "com.typesafe.play" %% "play-slick-evolutions" % "2.0.2",
    "mysql" % "mysql-connector-java" % "5.1.41"
  )

  val scalaV = scalaVersion := "2.11.8"

  val settings = Seq(
    scalaV,
    organization := "yangtuo.peng"
  )
}