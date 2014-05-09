package clairvoyance.specs2.export

import clairvoyance.export.ClairvoyanceHtml
import org.specs2.runner.SpecificationsFinder
import org.specs2.main.Arguments
import org.specs2.specification.ExecutedSpecification

trait ClairvoyanceHtmlPrinter {

  def print(executedSpec: ExecutedSpecification)(implicit args: Arguments): Seq[ClairvoyanceHtml] =
    Seq(ClairvoyanceHtml(executedSpec.name.url, printHtml(executedSpec.name.title, executedSpec).xml))

  private def printHtml(specificationTitle: String, spec: ExecutedSpecification)(implicit args: Arguments): ClairvoyanceHtmlFormat =
    clairvoyanceFormat.printHtml(
      clairvoyanceFormat
        .printHead(specificationTitle)
        .printSidebar(SpecificationsFinder.specifications(pattern = ".*[Example|Spec]"))
        .printBody(specificationTitle, spec, printFragmentsOf(spec).xml)
        .xml
    )

  private def printFragmentsOf(spec: ExecutedSpecification)(implicit args: Arguments): ClairvoyanceHtmlFormat =
    spec.fragments.foldLeft(clairvoyanceFormat) { (htmlFormat, fragment) => htmlFormat.printFragment(spec, fragment) }

  private def clairvoyanceFormat = new ClairvoyanceHtmlFormat()
}
