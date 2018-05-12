import sbt.{Def, _}

object Dependencies {

  val akkaHttpVersion = "10.0.10"
  val scalaTestVersion = "3.0.1"
  val javaMailVersion = "1.4"
  val cassandraVersion = "2.1.10.3"
  val confServiceVersion="1.2.1"
  val loggerVersion="1.7.25"
  def compileDependencies(deps: List[ModuleID]): Seq[ModuleID] = deps map (_ % Compile)

  def providedDependencies(deps: List[ModuleID]): Seq[ModuleID] = deps map (_ % Provided)

  def testDependencies(deps: List[ModuleID]): Seq[ModuleID] = deps map (_ % Test)


  def testClassifierDependencies(deps: List[ModuleID]) = deps map (_ % "test" classifier "tests")

  def akkaHttp: Def.Initialize[List[ModuleID]] = Def.setting {
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion :: Nil
  }

  def akkaHttpTest: Def.Initialize[List[ModuleID]] = Def.setting {
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test :: Nil
  }

  def scalaTest: Def.Initialize[List[ModuleID]] = Def.setting {
    "org.scalatest" %% "scalatest" % scalaTestVersion % Test :: Nil
  }

  def akkaJson: Def.Initialize[List[ModuleID]] = Def.setting {
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion :: Nil
  }

  def javaMailer: Def.Initialize[List[ModuleID]] = Def.setting {
    "javax.mail" % "mail" % javaMailVersion :: Nil
  }

 def typesafeConfService: Def.Initialize[List[ModuleID]] =Def.setting{
   "com.typesafe" % "config" % confServiceVersion :: Nil
 }
  def loggers=Def.setting{
    "org.slf4j" % "slf4j-api" % loggerVersion :: "org.slf4j" % "slf4j-simple" % "1.7.25" :: Nil
  }


}
