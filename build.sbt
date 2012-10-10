name := "clairvoyance"

organization := "rhyskeepence"

version := "17"

scalaVersion := "2.9.2"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.12.1",
  "org.pegdown" % "pegdown" % "1.0.2",
  "net.sourceforge.plantuml" % "plantuml" % "7933"
)

resolvers ++= Seq(
  "sonatype releases"  at "http://oss.sonatype.org/content/repositories/releases"
)

publishTo := Some(Resolver.file("rhys github", new File(Path.userHome.absolutePath+ "/code/mvn-repo/releases")))
