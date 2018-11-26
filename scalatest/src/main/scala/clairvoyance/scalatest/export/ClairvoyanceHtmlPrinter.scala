package clairvoyance.scalatest.export

import clairvoyance.export.ClairvoyanceHtml

trait ClairvoyanceHtmlPrinter {

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
        .printBody(suiteResult.suiteName, suiteResult)
        .xml
    )
  }

  protected def allSuiteResults: Seq[SuiteResult]
  private def clairvoyanceFormat = new ScalaTestHtmlFormat()
}
