package org.specs2.clairvoyance.export

import org.specs2.io.ConsoleOutput
import org.specs2.main.Arguments
import org.specs2.reporter._
import org.specs2.specification._
import scalaz.Scalaz._
import scala.xml.NodeSeq

class ClairvoyanceHtmlExporting extends Exporter with ClairvoyanceHtmlPrinter with ClairvoyanceHtmlFileWriter with TeamCityTestReporter {
  type ExportType = Unit

  def export(implicit arguments: Arguments): ExecutingSpecification => ExecutedSpecification = (spec: ExecutingSpecification) => {
    val executed = spec.execute
    val args = arguments <| executed.arguments
    print(executed)(args) |> writeFiles(args)
    printTeamCityLog(executed)
    executed
  }
}

case class ClairvoyanceHtml(url: String, xml: NodeSeq)

trait HtmlReporter extends DefaultReporter with ClairvoyanceHtmlFileWriter with ConsoleOutput