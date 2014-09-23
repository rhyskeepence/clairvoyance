package clairvoyance.scalatest.export

import clairvoyance.scalatest.export.ResultExtractor.extract
import org.scalatest.Reporter
import org.scalatest.events.Event

import scala.collection.mutable.ListBuffer

/** required by IDEA's ScalaTest runner only */
class ScalaTestHtmlReporterWithLocation extends ScalaTestHtmlReporter

class ScalaTestHtmlReporter extends Reporter {
  private var events: ListBuffer[Event] = new ListBuffer[Event]

  private val startTime: Long = System.currentTimeMillis()

  override def apply(event: Event): Unit = events += event

  def done(): Unit = writeResult(extract(events, System.currentTimeMillis() - startTime))

  private def writeResult(result: Option[SuiteResult]): Unit = new SingleClairvoyanceHtmlFileWriter().writeFiles(result.map(new SingleClairvoyanceHtmlPrinter().print).toSeq)
}
