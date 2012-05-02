name := "clairvoyance"

organization := "rhyskeepence"

version := "13"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.9",
  "org.pegdown" % "pegdown" % "1.0.2",
  "net.sourceforge.plantuml" % "plantuml" % "6487",
  "junit" % "junit" % "4.8"
)

resolvers ++= Seq(
  "sonatype releases"  at "http://oss.sonatype.org/content/repositories/releases"
)

publishTo := Some(Resolver.file("rhys github", new File(Path.userHome.absolutePath+ "/code/mvn-repo/releases")))
