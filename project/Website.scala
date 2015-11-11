import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtSite.site
import com.typesafe.sbt.SbtSite.SiteKeys.{siteSourceDirectory, siteMappings}

object Website {

  lazy val settings = site.settings ++ Seq[Setting[_]](
    siteSourceDirectory <<= baseDirectory (_ / "site"),
    siteMappings <++= baseDirectory map { (b) =>
      (b / "specs2" / "target" / "clairvoyance-reports" ** "*" pair rebase(b / "specs2" / "target" / "clairvoyance-reports", "clairvoyance-reports/specs2")) ++
        (b / "scalatest" / "target" / "clairvoyance-reports" ** "*" pair rebase(b / "scalatest" / "target" / "clairvoyance-reports", "clairvoyance-reports/scalatest"))
    }
  )
}
