import sbt.Keys._
import sbt._
import sbtassembly.AssemblyKeys._
import sbtassembly.{AssemblyKeys, MergeStrategy, PathList, ShadeRule}

object ProjectSetting {

  lazy val sbtAssemblySettings = Seq(
    assemblyShadeRules in assembly := Seq(
      ShadeRule
        .rename("io.netty.**" -> "shadeio.@1")
        .inLibrary("io.netty" % "netty-all" % "4.0.23.Final")
        .inLibrary("org.apache.cassandra" % "cassandra-all" % "2.1.9")
    ),
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs@_*) => MergeStrategy.discard
      case "application.conf" => MergeStrategy.last
      case "reference.conf" => MergeStrategy.concat
      case x => MergeStrategy.first
    }
  )
  val projectSettings = Defaults.defaultSettings ++
    Seq(
      organization := "com.knoldus",
      scalaVersion in ThisBuild := "2.11.8",
      version := "1.0",
      fork in Test := true,
      parallelExecution in Test := false
    )

  def BaseProject(name: String): Project = Project(name, file(name))
    .settings(
      Defaults.coreDefaultSettings ++ projectSettings
    )
}
