import scala.util.Properties.setProp

lazy val moduleSettings = Common.settings ++ Publication.settings

lazy val clairvoyance = (project in file("."))
  .settings(moduleSettings: _*)
  .settings(packagedArtifacts := Map.empty)
  .aggregate(core, scalatest)

lazy val core = (project in file("core"))
  .settings(moduleSettings: _*)
  .settings(
    name := "clairvoyance-core",
    libraryDependencies ++= Seq(
      "net.sourceforge.plantuml" % "plantuml" % "8046",
      "org.pegdown"              % "pegdown"  % "1.6.0"
    )
  )

lazy val scalatest = (project in file("scalatest"))
  .settings(moduleSettings: _*)
  .settings(
    name := "clairvoyance-scalatest",
    libraryDependencies ++= Seq(
      "org.scalatest"  %% "scalatest"  % "3.0.8"  % "provided",
      "org.scalacheck" %% "scalacheck" % "1.14.1" % "test"
    ),
    testOptions in Test += Tests.Setup(() => {
      setProp("scalatest.output.dir", s"${target.value.getAbsolutePath}/clairvoyance-reports/")
    })
  ) dependsOn core
