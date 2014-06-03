package clairvoyance.specs2.export

import clairvoyance.export.ClairvoyanceHtmlFileWriter
import org.specs2.io.ConsoleOutput
import org.specs2.main.Arguments
import org.specs2.reporter.{DefaultReporter, Exporter}
import org.specs2.specification.{ExecutedSpecification, ExecutingSpecification}
import scalaz.Scalaz.ToIdOps

class ClairvoyanceHtmlExporting extends Exporter with ClairvoyanceHtmlPrinter with ClairvoyanceHtmlFileWriter with TeamCityTestReporter {
  type ExportType = Unit

  def export(implicit arguments: Arguments): ExecutingSpecification => ExecutedSpecification = (spec: ExecutingSpecification) => {
    val executed = spec.execute
    val args = arguments <| executed.arguments
    print(executed)(args) |> writeFiles
    printTeamCityLog(executed)
    executed
  }
}

trait HtmlReporter extends DefaultReporter with ClairvoyanceHtmlFileWriter with ConsoleOutput
