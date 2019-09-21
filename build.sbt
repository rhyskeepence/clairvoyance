import scala.util.Properties.{propOrEmpty, setProp}

lazy val moduleSettings = Common.settings ++ Publication.settings

lazy val clairvoyance = (project in file("."))
  .settings(moduleSettings: _*)
  .settings(packagedArtifacts := Map.empty)
  .aggregate(core, specs2, scalatest)

lazy val core = (project in file("core"))
  .settings(moduleSettings: _*)
  .settings(name := "clairvoyance-core",
    libraryDependencies ++= Seq(
      "net.sourceforge.plantuml" % "plantuml" % "8046",
      "org.pegdown" % "pegdown" % "1.6.0",
      "org.scala-lang.modules" %% "scala-xml" % "1.0.6"
    )
  )

val specs2Version = "2.4.17"

lazy val specs2 = (project in file("specs2"))
  .settings(moduleSettings: _*)
  .settings(name := "clairvoyance-specs2",
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-core" % specs2Version % "provided",
      "org.specs2" %% "specs2-scalacheck" % specs2Version % "provided"
    ),
    testOptions in Test += Tests.Setup(() => {
      setProp("specs2.outDir", s"${target.value.getAbsolutePath}/clairvoyance-reports/")
      setProp("specs2.srcTestDir", (scalaSource in Test).value.getAbsolutePath)
      setProp("specs2.statsDir", s"${propOrEmpty("specs2.outDir")}/stats/")
    })
  ) dependsOn core

lazy val scalatest = (project in file("scalatest"))
  .settings(moduleSettings: _*)
  .settings(name := "clairvoyance-scalatest",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.5" % "provided",
      "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
    ),
    testOptions in Test += Tests.Setup(() => {
      setProp("scalatest.output.dir", s"${target.value.getAbsolutePath}/clairvoyance-reports/")
    })
  ) dependsOn core

