package clairvoyance.specs2.export

import clairvoyance.export.ClairvoyanceHtmlFileWriter
import org.specs2.io.ConsoleOutput
import org.specs2.main.Arguments
import org.specs2.reporter.{DefaultReporter, Exporter}
import org.specs2.specification.{ExecutedSpecification, ExecutingSpecification}
import scala.util.Properties.{propOrElse, userDir}

class ClairvoyanceHtmlExporting
    extends Exporter
    with ClairvoyanceHtmlPrinter
    with ClairvoyanceHtmlFileWriter
    with TeamCityTestReporter {
  type ExportType = Unit

  def export(implicit arguments: Arguments): ExecutingSpecification => ExecutedSpecification =
    (spec: ExecutingSpecification) => {
      val executed = spec.execute
      val args     = arguments <| executed.arguments
      writeFiles(print(executed)(args))
      printTeamCityLog(executed)
      executed
    }

  protected def outputDir = propOrElse("specs2.outDir", s"$userDir/target/clairvoyance-reports/")
}

trait HtmlReporter extends DefaultReporter with ClairvoyanceHtmlFileWriter with ConsoleOutput
