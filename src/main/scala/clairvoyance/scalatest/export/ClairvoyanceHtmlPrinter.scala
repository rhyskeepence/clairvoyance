package clairvoyance.scalatest.export

import clairvoyance.export.ClairvoyanceHtml
import org.scalatest.tools.clairvoyance.ScalaTestSpy.SuiteResult

trait ClairvoyanceHtmlPrinter {

  def allSuiteResults: Seq[SuiteResult]

  def print(suiteResult: SuiteResult): ClairvoyanceHtml = {
    val suiteFileName = asFileName(suiteResult)
    ClairvoyanceHtml(s"$suiteFileName.html", printHtml(suiteFileName, suiteResult).xml)
//    printTeamCityLog(executed)
  }

  private def asFileName(suiteResult: SuiteResult) = suiteResult.suiteClassName match {
    case Some(suiteClassName) => suiteClassName
    case None => suiteResult.suiteName
  }

  private def printHtml(specificationTitle: String, suiteResult: SuiteResult): ScalaTestHtmlFormat = {
    clairvoyanceFormat.printHtml(
      clairvoyanceFormat
        .printHead(specificationTitle)
        .printSidebar(allSuiteResults)
        .printBody(suiteResult.suiteName, suiteResult)
        .xml
    )
  }

  private def clairvoyanceFormat = new ScalaTestHtmlFormat()
}
