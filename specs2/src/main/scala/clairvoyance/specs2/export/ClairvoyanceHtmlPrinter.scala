package clairvoyance.specs2.export

import clairvoyance.export.{ClairvoyanceHtml, HtmlFormat}
import org.specs2.main.Arguments
import org.specs2.runner.SpecificationsFinder
import org.specs2.specification.{ExecutedSpecification, SpecificationStructure}

trait ClairvoyanceHtmlPrinter {

  val allSpecs = findSpecs(pattern = ".*Example") ++ findSpecs(pattern = ".*Spec")

  def print(spec: ExecutedSpecification)(implicit args: Arguments): Seq[ClairvoyanceHtml] =
    Seq(
      ClairvoyanceHtml(spec.name.url, printHtml(spec.name.fullName, spec.name.title, spec).xml),
      ClairvoyanceHtml("index.html", printIndex(), notifyUser = false)
    )

  private def printHtml(specificationFullName: String, specificationTitle: String, spec: ExecutedSpecification)
                       (implicit args: Arguments): HtmlFormat =
    clairvoyanceFormat.printHtml(
      clairvoyanceFormat
        .printHead(specificationTitle)
        .printSidebar(allSpecs)
        .printBody(specificationTitle, spec, printFragmentsOf(specificationFullName, spec).xml)
        .xml
    )

  private def printFragmentsOf(specificationFullName: String, spec: ExecutedSpecification)
                              (implicit args: Arguments): HtmlFormat =
    spec.fragments.foldLeft(clairvoyanceFormat) {
      (htmlFormat, fragment) => htmlFormat.printFragment(specificationFullName, fragment)
    }

  private def clairvoyanceFormat = new Specs2HtmlFormat()

  private def printIndex()(implicit args: Arguments) = new Specs2IndexHtmlFormat().printHtml(allSpecs)

  private def findSpecs(pattern: String): Seq[SpecificationStructure] =
    SpecificationsFinder.specifications(pattern = pattern, basePath = new java.io.File(".").getAbsolutePath).sortBy(_.identification.title)
}
