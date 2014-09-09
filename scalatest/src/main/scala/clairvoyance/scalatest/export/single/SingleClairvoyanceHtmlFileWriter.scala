package clairvoyance.scalatest.export.single

import clairvoyance.export.{ClairvoyanceHtml, ClairvoyanceHtmlFileWriter}

import scala.util.Properties.{propOrElse, userDir}

class SingleClairvoyanceHtmlFileWriter extends ClairvoyanceHtmlFileWriter {
  override def writeFile: (ClairvoyanceHtml) => Unit = super.writeFile

  override protected def outputDir: String = propOrElse("scalatest.output.dir", s"$userDir/target/clairvoyance-reports/")
}
