name := "Kip-Portal"

version := "1.0"
scalaVersion := "2.11.8"

import Dependencies._
import ProjectSetting._

lazy val root = Project("backend", file("."))
  .settings(commands += Command.command("testUntilFailed") { state =>
    "test" :: "testUntilFailed" :: state
  })
  .settings(sbtAssemblySettings: _*)
  .aggregate(userApi, notificationApi, persistence, common)

lazy val common = BaseProject("common").settings(
  libraryDependencies ++= compileDependencies(typesafeConfService.value ++ loggers.value ++ sprayJson.value
  )
    ++ testClassifierDependencies(Nil))

lazy val persistence = BaseProject("persistence").settings(
  libraryDependencies ++= compileDependencies(cassandraPhantom.value ++ scalaReflection.value)
    ++ testClassifierDependencies(Nil) ++ testDependencies(scalaTest.value ++ embeddedCassandra.value)).dependsOn(common)
  .settings(sbtAssemblySettings: _*)

lazy val userApi = BaseProject("user-api").settings(
  libraryDependencies ++= compileDependencies(akkaHttp.value ++ akkaJson.value ++ encryptionDependency.value ++ sprayJson.value ++ cassandraPhantom.value)
    ++ testClassifierDependencies(Nil) ++ testDependencies(akkaHttpTest.value ++ scalaTest.value)).dependsOn(notificationApi,common,persistence)

lazy val notificationApi = BaseProject("notification-api").settings(
  libraryDependencies ++= compileDependencies(akkaHttp.value ++ akkaJson.value ++ javaMailer.value ++ loggers.value)
    ++ testDependencies(akkaHttpTest.value ++ scalaTest.value ++ javaMailerMock.value)).dependsOn(common)

