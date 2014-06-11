import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtGhPages.ghpages
import com.typesafe.sbt.SbtGit.git
import com.typesafe.sbt.SbtSite.site
import com.typesafe.sbt.SbtSite.SiteKeys.{siteSourceDirectory, siteMappings}

import scala.util.Properties.{propOrEmpty, setProp}
import scala.util.Try

import xerial.sbt.Sonatype._
import SonatypeKeys._

object build extends Build {

  type Settings = Def.Setting[_]

  lazy val websiteSettings = site.settings ++ ghpages.settings ++ Seq[Setting[_]](
      git.remoteRepo := "git@github.com:rhyskeepence/clairvoyance.git",
      siteSourceDirectory <<= baseDirectory / "site",
      siteMappings <++= baseDirectory map { (b) =>
        (b / "specs2" / "target" / "clairvoyance-reports" ** "*" pair rebase(b / "specs2" / "target" / "clairvoyance-reports", "clairvoyance-reports/specs2")) ++
        (b / "scalatest" / "target" / "clairvoyance-reports" ** "*" pair rebase(b / "scalatest" / "target" / "clairvoyance-reports", "clairvoyance-reports/scalatest"))
      }
    )

  lazy val commonSettings = Seq(
    organization := "com.github.rhyskeepence",
    version := Try(sys.env("BUILD_NUMBER")).map("1.0." + _).getOrElse("1.0-SNAPSHOT"),
    scalaVersion := "2.10.4",
    crossScalaVersions := Seq("2.10.4", "2.11.1"),
    javacOptions ++= Seq("-Xmx3G", "-Xms512m", "-Xss4m"),
    scalacOptions in GlobalScope ++= Seq("-Xcheckinit", "-Xlint", "-deprecation", "-unchecked", "-feature", "-language:implicitConversions,reflectiveCalls,postfixOps,higherKinds,existentials")
  )

  lazy val moduleSettings = commonSettings ++ publicationSettings ++ websiteSettings

  lazy val clairvoyance = (project in file(".")).
    settings(moduleSettings: _*).
    settings(
      aggregate in sonatypeReleaseAll := false,
      packagedArtifacts := Map.empty
    ).aggregate(core, specs2, scalatest)

  lazy val core = (project in file("core"))
    .settings(moduleSettings: _*)
    .settings(
      name := "clairvoyance-core",
      libraryDependencies <<= scalaVersion { scala_version => Seq(
        "com.github.scala-incubator.io" %% "scala-io-file"  % "0.4.3",
        "net.sourceforge.plantuml"      %  "plantuml"       % "7999",
        "org.pegdown"                   %  "pegdown"        % "1.4.2",
        "org.scala-lang"                %  "scala-compiler" % scala_version % "optional"
      ) ++ (CrossVersion.partialVersion(scala_version) match {
        case Some((2, scalaMajor)) if scalaMajor >= 11 => Seq(
          "org.scala-lang.modules" %% "scala-xml" % "1.0.2",
          "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.1"
        )
        case _ => Seq.empty
      })}
    )

  lazy val specs2 = (project in file("specs2"))
    .settings(moduleSettings: _*)
    .settings(name := "clairvoyance-specs2",
      libraryDependencies := Seq(
        "org.specs2"     %% "specs2"     % "2.3.12",
        "org.scalacheck" %% "scalacheck" % "1.11.4" % "test"
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
      libraryDependencies := Seq(
        "org.scalatest"  %% "scalatest"   % "2.2.0-RC2",
        "org.scalaz"     %% "scalaz-core" % "7.0.6",
        "org.scalacheck" %% "scalacheck"  % "1.11.4" % "test"
      ),
      testOptions in Test += Tests.Setup(() => {
        setProp("scalatest.output.dir", s"${target.value.getAbsolutePath}/clairvoyance-reports/")
      }),
      testOptions in Test += Tests.Argument(
        TestFrameworks.ScalaTest, "-C", "clairvoyance.scalatest.export.ScalaTestHtmlReporter"
      )
    ) dependsOn core

  lazy val publicationSettings: Seq[Settings] = Seq(
    publishTo <<= version { v: String =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },

    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },

    credentials += Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", System.getenv("SONATYPE_USER"), System.getenv("SONATYPE_PASSWORD")),

    pomExtra :=
      <url>http://www.github.com/rhyskeepence/clairvoyance</url>
        <licenses>
          <license>
            <name>BSD-style</name>
            <url>http://www.opensource.org/licenses/bsd-license.php</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git://github.com/rhyskeepence/clairvoyance.git</url>
          <connection>scm:git://github.com/rhyskeepence/clairvoyance.git</connection>
        </scm>
        <developers>
          <developer>
            <id>rhyskeepence</id>
            <name>Rhys Keepence</name>
            <url>http://rhyskeepence.github.com</url>
          </developer>
          <developer>
            <id>franckrasolo</id>
            <name>Franck Rasolo</name>
            <url>https://github.com/franckrasolo</url>
          </developer>
        </developers>

  ) ++ sonatypeSettings
}
