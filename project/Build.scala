import com.google.javascript.jscomp.{CompilerOptions, CompilationLevel}
import java.util.TimeZone
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
    cache,
    filters,
    "com.typesafe.play" % "play_2.10" % "2.2.0",
    "securesocial" %% "securesocial" % "master-SNAPSHOT" exclude("org.scala-stm", "scala-stm_2.10.0"),
    "org.postgresql" % "postgresql" % "9.2-1002-jdbc4",
    "commons-validator" % "commons-validator" % "1.4.0" ,
    "org.ocpsoft.prettytime" % "prettytime" % "3.0.2.Final",
    "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
    "com.github.mumoshu" %% "play2-memcached" % "0.3.0.2",
    "com.jolbox" % "bonecp" % "0.8.0-rc1",
    "org.bouncycastle" % "bcprov-jdk15on" % "1.49",
    "org.bouncycastle" % "bcpkix-jdk15on" % "1.49"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += Resolver.url("sbt-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns),
    resolvers += "Spy Repository" at "http://files.couchbase.com/maven2",
    lessEntryPoints <<= baseDirectory(_ / "app" / "assets" / "css" ** "global.less"),
    scalacOptions += "-feature",
    routesImport += "extensions.Binders._",
    requireJs += "main.js",
    requireJsShim := "main.js"
  )
}
