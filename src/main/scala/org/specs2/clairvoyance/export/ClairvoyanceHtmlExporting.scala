package org.specs2.clairvoyance.export

import org.specs2.io.ConsoleOutput
import org.specs2.main.Arguments
import org.specs2.reporter._
import org.specs2.specification._
import org.specs2.internal.scalaz.Scalaz._
import scala.xml.{Xhtml, NodeSeq}
import java.io.{File, Writer}
import java.net.URL

class ClairvoyanceHtmlExporting extends Exporter with ClairvoyanceHtmlPrinter with ClairvoyanceHtmlFileWriter with TeamCityTestReporter {
  type ExportType = Unit

  def export(implicit args: Arguments): ExecutingSpecification => ExecutedSpecification = (spec: ExecutingSpecification) => {
    val executed = spec.execute
    print(executed) |> writeFiles
    printTeamCityLog(executed)
    executed
  }
}

case class ClairvoyanceHtml(url: String, xml: NodeSeq)

trait HtmlReporter extends DefaultReporter with ClairvoyanceHtmlFileWriter with ConsoleOutput