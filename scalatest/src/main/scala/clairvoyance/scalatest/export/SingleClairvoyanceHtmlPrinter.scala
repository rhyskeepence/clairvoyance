package clairvoyance.scalatest.export

import clairvoyance.export.ClairvoyanceHtml

class SingleClairvoyanceHtmlPrinter extends ClairvoyanceHtmlPrinter {
  private var result: SuiteResult = _

  override def print(result: SuiteResult): ClairvoyanceHtml = {
    this.result = result
    super.print(result)
  }

  override protected def allSuiteResults: Seq[SuiteResult] = Seq(result)
}
