import sbt._
import sbt.Keys._

import scala.util.Try

object Common {
  val scala_2_12 = "2.12.8"
  val scala_2_13 = "2.13.0"

  lazy val settings = Seq(
    organization := "com.github.rhyskeepence",
    version := Try(sys.env("BUILD_NUMBER")).map("1.0." + _).getOrElse("1.0-SNAPSHOT"),
    scalaVersion := scala_2_13,
    crossScalaVersions := Seq(scala_2_12, scala_2_13),
    scalacOptions in GlobalScope ++= Seq(
      "-Xcheckinit", "-Xlint", "-deprecation", "-unchecked", "-feature",
      "-language:implicitConversions,reflectiveCalls,postfixOps,higherKinds,existentials"
    )
  )
}
