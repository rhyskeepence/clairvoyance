import scala.util.Try
import SonatypeKeys._

name := "clairvoyance"

organization := "com.github.rhyskeepence"

version := Try(sys.env("BUILD_NUMBER")).map("1.0." + _).getOrElse("1.0-SNAPSHOT")

scalaVersion := "2.11.1"

crossScalaVersions := Seq("2.10.4", "2.11.1")

libraryDependencies <<= scalaVersion { scala_version => Seq(
  "org.specs2" %% "specs2" % "2.3.12",
  "org.scalatest" %% "scalatest" % "2.2.0-RC1",
  "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.3",
  "org.pegdown" % "pegdown" % "1.4.2",
  "net.sourceforge.plantuml" % "plantuml" % "7999",
  "org.scalacheck" %% "scalacheck" % "1.11.4" % "optional",
  "org.scala-lang" % "scala-compiler" % scala_version  % "optional"
)}

libraryDependencies := {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, scalaMajor)) if scalaMajor >= 11 => libraryDependencies.value :+ "org.scala-lang.modules" %% "scala-xml" % "1.0.1"
    case _ => libraryDependencies.value
  }
}

resolvers ++= Seq(
  "sonatype releases"  at "http://oss.sonatype.org/content/repositories/releases"
)

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-language:implicitConversions,reflectiveCalls,postfixOps,higherKinds,existentials")

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-C", "clairvoyance.scalatest.export.ScalaTestHtmlReporter")

sonatypeSettings

pgpPassphrase := Some(Try(sys.env("SECRET")).getOrElse("goaway").toCharArray)

pgpSecretRing := file("./publish/sonatype.asc")

publishTo <<= version { v: String =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

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
