import sbt._
import sbt.Keys._

import scala.util.Try

object Common {
  val scala_2_11 = "2.11.8"

  lazy val settings = Seq(
    organization := "com.github.rhyskeepence",
    version := Try(sys.env("BUILD_NUMBER")).map("1.0." + _).getOrElse("1.0-SNAPSHOT"),
    scalaVersion := scala_2_11,
    crossScalaVersions := Seq("2.10.6", scala_2_11),
    scalacOptions in GlobalScope ++= Seq(
      "-Xcheckinit", "-Xlint", "-deprecation", "-unchecked", "-feature",
      "-language:implicitConversions,reflectiveCalls,postfixOps,higherKinds,existentials"
    ),
    updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true)
  )
}
