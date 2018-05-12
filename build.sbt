name := "Kip-Portal"

version := "1.0"
scalaVersion := "2.11.8"

import Dependencies._
import ProjectSetting._

lazy val root = Project("kip-portal", file("."))
  .settings(commands += Command.command("testUntilFailed") { state =>
    "test" :: "testUntilFailed" :: state
  })
  .settings(sbtAssemblySettings: _*)
  .aggregate(userApi, notificationApi,  persistance)

lazy val common = BaseProject("common").settings(
  libraryDependencies ++= compileDependencies(typesafeConfService.value ++ loggers.value)
    ++ testClassifierDependencies(Nil))

lazy val userApi = BaseProject("user-api").settings(
  libraryDependencies ++= compileDependencies(akkaHttp.value ++ akkaJson.value)
    ++ testClassifierDependencies(Nil)).dependsOn(common)

lazy val persistance = BaseProject("persistance").settings(
  libraryDependencies ++= compileDependencies(akkaHttp.value)
    ++ testClassifierDependencies(Nil)).dependsOn(common)
  .settings(sbtAssemblySettings: _*)


lazy val notificationApi = BaseProject("notification-api").settings(
  libraryDependencies ++= compileDependencies(akkaHttp.value ++ akkaJson.value ++ javaMailer.value)
    ++ testClassifierDependencies(Nil)).dependsOn(common)

