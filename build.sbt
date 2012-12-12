name := "clairvoyance"

organization := "com.github.rhyskeepence"

version := "27"

scalaVersion := "2.9.2"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.12.3",
  "org.pegdown" % "pegdown" % "1.0.2",
  "net.sourceforge.plantuml" % "plantuml" % "7933"
)

resolvers ++= Seq(
  "sonatype releases"  at "http://oss.sonatype.org/content/repositories/releases"
)

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

pomExtra := (
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
  </developers>
)

