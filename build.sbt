name := "clairvoyance"

organization := "rhyskeepence"

version := "5"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.9-SNAPSHOT",
  "org.pegdown" % "pegdown" % "1.0.2",
  "junit" % "junit" % "4.8"
)

resolvers ++= Seq(
  "sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "sonatype releases"  at "http://oss.sonatype.org/content/repositories/releases"
)

publishTo := Some(Resolver.file("rhys github", new File(Path.userHome.absolutePath+ "/code/mvn-repo/releases")))