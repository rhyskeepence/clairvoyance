package clairvoyance.scalatest.export.single

import clairvoyance.export.ClairvoyanceHtml
import clairvoyance.scalatest.export.{ClairvoyanceHtmlPrinter, SuiteResult}

class SingleClairvoyanceHtmlPrinter extends ClairvoyanceHtmlPrinter {
  private var result: SuiteResult = _

  override def print(result: SuiteResult): ClairvoyanceHtml = {
    this.result = result
    super.print(result)
  }

  override protected def allSuiteResults: Seq[SuiteResult] = Seq(result)
}
