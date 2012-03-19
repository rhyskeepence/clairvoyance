name := "clairvoyance"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.8.2",
  "org.pegdown" % "pegdown" % "1.0.2",
  "junit" % "junit" % "4.8"
)

resolvers ++= Seq(
  "sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "sonatype releases"  at "http://oss.sonatype.org/content/repositories/releases"
)
