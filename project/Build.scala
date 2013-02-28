import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "matrix-decider"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq("com.typesafe.slick" %% "slick" % "1.0.0",
    "org.scalatest" %% "scalatest" % "2.0.M5b" % "test",
    "com.typesafe" %% "scalalogging-slf4j" % "1.0.1",
    "com.typesafe.akka" %% "akka-testkit" % "2.1.0" % "test"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here  
  )

}
