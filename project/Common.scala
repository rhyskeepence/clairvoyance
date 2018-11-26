import sbt._
import sbt.Keys._

import scala.util.Try

object Common {
  val scala_2_12 = "2.12.7"

  lazy val settings = Seq(
    organization := "com.github.rhyskeepence",
    version := Try(sys.env("BUILD_NUMBER")).map("1.0." + _).getOrElse("1.0-SNAPSHOT"),
    scalaVersion := scala_2_12,
    crossScalaVersions := Seq("2.11.8", scala_2_12),
    scalacOptions in GlobalScope ++= Seq(
      "-Xcheckinit", "-Xlint", "-deprecation", "-unchecked", "-feature",
      "-language:implicitConversions,reflectiveCalls,postfixOps,higherKinds,existentials"
    ),
    updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true)
  )
}
