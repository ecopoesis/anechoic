import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "anechoic"
  val appVersion      = "1-SNAPSHOT"

  val appDependencies = Seq(
    jdbc,
    anorm,
    "securesocial" %% "securesocial" % "master-SNAPSHOT",
    "org.postgresql" % "postgresql" % "9.2-1002-jdbc4"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += Resolver.url("sbt-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)
  )

}
