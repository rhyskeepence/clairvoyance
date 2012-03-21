package org.specs2.clairvoyance.output

import org.specs2.specification.ExecutedSpecification
import org.specs2.main.Arguments

trait ClairvoyanceHtmlPrinter {

  def clairvoyanceFormat: ClairvoyanceHtmlFormat = new ClairvoyanceHtmlFormat()

  def print(executedSpec: ExecutedSpecification)(implicit args: Arguments) = {
    Seq(ClairvoyanceHtml(executedSpec.name.url, printHtml(executedSpec).xml))
  }

  def printHtml(spec: ExecutedSpecification) = {
    clairvoyanceFormat.printHtml(
      clairvoyanceFormat
        .printHead(spec)
        .printBody(spec, printFragmentsOf(spec).xml)
        .xml
    )
  }

  def printFragmentsOf(spec: ExecutedSpecification) = {
    spec.fragments.foldLeft(clairvoyanceFormat) {
      (res, cur) =>
        res.printFragment(spec, cur)
    }
  }
}
