package clairvoyance.scalatest.export

import clairvoyance.scalatest.export.ResultExtractor.extract
import clairvoyance.scalatest.export.SingleClairvoyanceHtmlPrinter
import org.scalatest.Reporter
import org.scalatest.events.Event

import scala.collection.mutable.ListBuffer

class ScalaTestHtmlReporter extends Reporter {
  private var events: ListBuffer[Event] = new ListBuffer[Event]

  private val startTime: Long = System.currentTimeMillis()

  override def apply(event: Event): Unit = events += event

  def done(): Unit = writeResult(extract(events, System.currentTimeMillis() - startTime))

  private def writeResult(result: Option[SuiteResult]): Unit = new SingleClairvoyanceHtmlFileWriter().writeFiles(result.map(new SingleClairvoyanceHtmlPrinter().print).toSeq)
}