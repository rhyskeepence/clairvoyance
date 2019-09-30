package clairvoyance.scalatest.export

import clairvoyance.export.ClairvoyanceHtml

object ClairvoyanceHtmlPrinter {

  def print(suiteResult: SuiteResult): ClairvoyanceHtml = {
    val suiteFileName = asFileName(suiteResult)
    ClairvoyanceHtml(s"$suiteFileName.html", printHtml(suiteFileName, suiteResult))
  }

  private def asFileName(suiteResult: SuiteResult) = suiteResult.suiteClassName match {
    case Some(suiteClassName) => suiteClassName
    case None                 => suiteResult.suiteName
  }

  private def printHtml(
      specificationTitle: String,
      suiteResult: SuiteResult
  ): String = {
    clairvoyanceFormat.format(specificationTitle, suiteResult)
  }

  private def clairvoyanceFormat = new ScalaTestHtmlFormat()
}
