import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtPgp.autoImportImpl._

import scala.util.Try

object Publication {

  lazy val settings = Seq(
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (version.value.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },

    publishMavenStyle := true,
    sources in (Compile, doc) := Seq.empty,
    mappings in (Compile, packageDoc) += (baseDirectory.value / "src" / "main" / "README") → "README",
    publishArtifact in Test := false,
    pomIncludeRepository := { _ ⇒ false },

    credentials += Credentials(
      realm = "Sonatype Nexus Repository Manager",
      host  = "oss.sonatype.org",
      System.getenv("SONATYPE_USER"),
      System.getenv("SONATYPE_PASSWORD")
    ),

    pgpPassphrase := Some(Try(sys.env("SECRET")).getOrElse("goaway").toCharArray),
    pgpSecretRing := file("./scripts/sonatype.asc"),

    pomExtra :=
      <url>https://www.github.com/rhyskeepence/clairvoyance</url>
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
  )
}
