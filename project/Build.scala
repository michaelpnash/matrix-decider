import sbt._
import sbt._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "matrix-decider"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq("com.typesafe.slick" %% "slick" % "1.0.0",
    "org.scalatest" %% "scalatest" % "2.0.M5b" % "test",
    "com.typesafe" %% "scalalogging-slf4j" % "1.0.1"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    sbt.Keys.fork in Test := false
  )

}
