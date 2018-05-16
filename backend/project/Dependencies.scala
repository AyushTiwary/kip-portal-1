import sbt.{Def, _}

object Dependencies {

  val akkaHttpVersion = "10.0.10"
  val scalaTestVersion = "3.0.1"
  val javaMailVersion = "1.4"
  val cassandraVersion = "2.1.10.3"
  val confServiceVersion = "1.2.1"
  val loggerVersion = "1.7.25"

  def compileDependencies(deps: List[ModuleID]): Seq[ModuleID] = deps map (_ % Compile)

  def providedDependencies(deps: List[ModuleID]): Seq[ModuleID] = deps map (_ % Provided)

  def testDependencies(deps: List[ModuleID]): Seq[ModuleID] = deps map (_ % Test)

  def testClassifierDependencies(deps: List[ModuleID]): List[ModuleID] = deps map (_ % "test" classifier "tests")

  def akkaHttp: Def.Initialize[List[ModuleID]] = Def.setting {
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion :: Nil
  }

  def akkaHttpTest: Def.Initialize[List[ModuleID]] = Def.setting {
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion :: Nil
  }

  def scalaTest: Def.Initialize[List[ModuleID]] = Def.setting {
    "org.scalatest" %% "scalatest" % scalaTestVersion :: "org.mockito" % "mockito-core" % "1.10.19" :: Nil
  }

  def embeddedCassandra: Def.Initialize[List[ModuleID]] = Def.setting {
    "org.cassandraunit" % "cassandra-unit" % "3.1.3.2" :: Nil
  }

  def akkaJson: Def.Initialize[List[ModuleID]] = Def.setting {
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion :: Nil
  }

  def javaMailer: Def.Initialize[List[ModuleID]] = Def.setting {
    "javax.mail" % "mail" % javaMailVersion :: Nil
  }

  def typesafeConfService: Def.Initialize[List[ModuleID]] = Def.setting {
    "com.typesafe" % "config" % confServiceVersion :: Nil
  }

  def loggers = Def.setting {
    "org.slf4j" % "slf4j-api" % loggerVersion :: "org.slf4j" % "slf4j-simple" % "1.7.25" :: Nil
  }

  def encryptionDependency: Def.Initialize[List[ModuleID]] = Def.setting {
    "org.mindrot" % "jbcrypt" % "0.3m" :: Nil
  }

  def sprayJson: Def.Initialize[List[ModuleID]] = Def.setting {
    "io.spray" %% "spray-json" % "1.3.4" :: "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.11.2" :: "org.json4s" %% "json4s-native" % "3.6.0-M3" :: "org.json4s" %% "json4s-ast" % "3.6.0-M3" :: Nil
  }

  def cassandraPhantom: Def.Initialize[List[ModuleID]] = Def.setting {
    "com.outworkers" %% "phantom-dsl" % "2.7.6" :: "com.outworkers" %% "phantom-connectors" % "2.7.6" :: Nil
  }
  def scalaReflection = Def.setting{
    "org.scala-lang" % "scala-reflect" % "2.11.8" :: Nil

  }
  def javaMailerMock =Def.setting{
    "org.jvnet.mock-javamail" % "mock-javamail" % "1.9" :: Nil
  }
  def cors: Def.Initialize[List[ModuleID]] = Def.setting{
    "ch.megard" %% "akka-http-cors" % "0.2.2" :: Nil
  }
}
