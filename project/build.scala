import sbt._
import sbt.Keys._

import scala.util.Properties.{propOrEmpty, setProp}

object build extends Build {
  private lazy val moduleSettings = Common.settings ++ Publication.settings ++ Website.settings

  lazy val clairvoyance = (project in file("."))
    .settings(moduleSettings: _*)
    .settings(packagedArtifacts := Map.empty)
    .aggregate(core, specs2, scalatest)

  lazy val core = (project in file("core"))
    .settings(moduleSettings: _*)
    .settings(name := "clairvoyance-core",
      libraryDependencies ++= Seq(
        "com.github.scala-incubator.io" %% "scala-io-file"  % "0.4.3"
          exclude("org.scala-lang.modules", s"scala-parser-combinators_${scalaVersion.value.substring(0, 4)}"),
        "net.sourceforge.plantuml"      %  "plantuml"       % "8046",
        "org.pegdown"                   %  "pegdown"        % "1.6.0",
        "org.scala-lang"                %  "scala-compiler" % scalaVersion.value % "optional"
      ),
      libraryDependencies ++= {
        CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, scalaMajor)) if scalaMajor >= 11 => Seq(
            "org.scala-lang.modules" %% "scala-xml" % "1.0.5",
            "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4"
          )
          case _ => Seq.empty
        }
      }
    )

  private val specs2Version = "[2.4.7,2.4.17]"

  lazy val specs2 = (project in file("specs2"))
    .settings(moduleSettings: _*)
    .settings(name := "clairvoyance-specs2",
      libraryDependencies ++= Seq(
        "org.specs2" %% "specs2-core"       % specs2Version % "provided",
        "org.specs2" %% "specs2-scalacheck" % specs2Version % "provided"
      ),
      testOptions in Test += Tests.Setup(() => {
        setProp("specs2.outDir",     s"${target.value.getAbsolutePath}/clairvoyance-reports/")
        setProp("specs2.srcTestDir", (scalaSource in Test).value.getAbsolutePath)
        setProp("specs2.statsDir",   s"${propOrEmpty("specs2.outDir")}/stats/")
      })
    ) dependsOn core

  lazy val scalatest = (project in file("scalatest"))
    .settings(moduleSettings: _*)
    .settings(name := "clairvoyance-scalatest",
      libraryDependencies ++= Seq(
        "org.scalatest"  %% "scalatest"  % "3.0.0"  % "provided",
        "org.scalacheck" %% "scalacheck" % "1.13.1" % "test"
      ),
      testOptions in Test += Tests.Setup(() => {
        setProp("scalatest.output.dir", s"${target.value.getAbsolutePath}/clairvoyance-reports/")
      })
    ) dependsOn core

//  lazy val proxy = (project in file("http-proxy"))
//    .settings(moduleSettings: _*)
//    .settings(name := "clairvoyance-http-proxy",
//      libraryDependencies := Seq(
//        "net.databinder" %% "unfiltered-netty-server" % "0.8.1",
//        "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
//        "org.slf4j" % "slf4j-nop" % "1.7.7" % "test"
//      )
//    ) dependsOn (core, specs2 % "test")
}
