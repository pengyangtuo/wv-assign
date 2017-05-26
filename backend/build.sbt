import payroll.Common
import sbt.Keys.{libraryDependencies, _}

name := """wv-payroll"""
version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .aggregate(service, report, employee)
  .settings(
    name := "wv-payroll"
  )

Common.scalaV
scalacOptions += "-feature"

lazy val report = (project in file("wv-report"))
  .enablePlugins(PlayScala)
  .dependsOn(service)
  .settings(Common.settings: _*)
  .settings(
    name := "wv-report",
    libraryDependencies ++= Common.dependencies,
    PlayKeys.devSettings := Seq("play.server.http.port" -> "9001") // default to listen on 9001
  )

lazy val employee = (project in file("wv-employee"))
  .enablePlugins(PlayScala)
  .dependsOn(service)
  .settings(Common.settings: _*)
  .settings(
    name := "wv-employee",
    libraryDependencies ++= Common.dependencies,
    PlayKeys.devSettings := Seq("play.server.http.port" -> "9000") // default to listen on 9000
  )

lazy val service = (project in file("wv-service"))
  .settings(Common.settings: _*)
  .settings(
    name := "wv-service",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.2"
  )