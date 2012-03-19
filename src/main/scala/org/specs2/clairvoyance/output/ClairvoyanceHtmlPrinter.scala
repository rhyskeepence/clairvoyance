package org.specs2.clairvoyance.output

import org.specs2.clairvoyance.ClairvoyantSpec
import org.specs2.specification.ExecutedSpecification
import org.specs2.main.Arguments

trait ClairvoyanceHtmlPrinter {

  def clairvoyanceFormat: ClairvoyanceHtmlFormat = new ClairvoyanceHtmlFormat()

  def print(sourceSpec: ClairvoyantSpec, executedSpec: ExecutedSpecification)(implicit args: Arguments) = {
    Seq(ClairvoyanceHtml(executedSpec.name.url, printHtml(sourceSpec, executedSpec).xml))
  }

  def printHtml(sourceSpec: ClairvoyantSpec, spec: ExecutedSpecification) = {
    clairvoyanceFormat.printHtml(
      clairvoyanceFormat
        .printHead(spec)
        .printBody(spec, printFragmentsOf(sourceSpec, spec).xml)
        .xml
    )
  }

  def printFragmentsOf(sourceSpec: ClairvoyantSpec, spec: ExecutedSpecification) = {
    spec.fragments.foldLeft(clairvoyanceFormat) {
      (res, cur) =>
        res.printFragment(sourceSpec, cur)
    }
  }
}
