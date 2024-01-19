import Dependencies._
import sbt.Keys.libraryDependencies


ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

ThisBuild / scalacOptions ++= Seq(
  "-Ywarn-value-discard",
  "-unchecked",
  "-target:11",
  "-feature",
  "-deprecation",
  "-Ywarn-dead-code",
  "-encoding", "UTF-8"
)

lazy val `simple_banking`: Project = (project in file("simple_banking"))
  .settings(
    name := "simple_banking",
    libraryDependencies ++= BankingDependencies.dependencies
  )

lazy val `simple-app` = (project in file ("."))
  .settings(
    publish / skip := true,
    publishLocal / skip := true
  )
  .aggregate(
    `simple_banking`
  )