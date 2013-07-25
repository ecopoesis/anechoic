import sbt._
import Keys._
import play.Project._
import com.typesafe.config._

object ApplicationBuild extends Build {
  val version = ConfigFactory.parseFile(new File("conf/version.conf")).resolve()

  val appName         = "anechoic"
  val appVersion      = version.getString("application.version")

  val appDependencies = Seq(
    jdbc,
    anorm,
    "securesocial" %% "securesocial" % "master-SNAPSHOT",
    "org.postgresql" % "postgresql" % "9.2-1002-jdbc4",
    "commons-validator" % "commons-validator" % "1.4.0" ,
    "org.ocpsoft.prettytime" % "prettytime" % "3.0.2.Final"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += Resolver.url("sbt-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns),
    lessEntryPoints <<= baseDirectory(_ / "app" / "assets" / "stylesheets" ** "global.less"),
    scalacOptions += "-feature",
    routesImport += "extensions.Binders._"
  )

}
