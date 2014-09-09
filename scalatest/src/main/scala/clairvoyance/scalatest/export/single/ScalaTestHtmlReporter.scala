package clairvoyance.scalatest.export.single

import clairvoyance.scalatest.export.SuiteResult
import clairvoyance.scalatest.export.single.ResultExtractor.extract
import org.scalatest.Reporter
import org.scalatest.events.Event

import scala.collection.mutable.ListBuffer

class ScalaTestHtmlReporter extends Reporter {
  private var events: ListBuffer[Event] = new ListBuffer[Event]

  private val startTime: Long = System.currentTimeMillis()

  override def apply(event: Event): Unit = events += event

  def done(): Unit = writeResult(extract(events, System.currentTimeMillis() - startTime))

  private def writeResult(result: Option[SuiteResult]): Unit = result.map(new SingleClairvoyanceHtmlPrinter().print).foreach(new SingleClairvoyanceHtmlFileWriter().writeFile)
}