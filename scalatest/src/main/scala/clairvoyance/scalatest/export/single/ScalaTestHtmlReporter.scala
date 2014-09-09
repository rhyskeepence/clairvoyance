package clairvoyance.scalatest.export.single

import clairvoyance.scalatest.export.SuiteResult
import clairvoyance.scalatest.export.single.ResultExtractor.extract
import org.scalatest.Reporter
import org.scalatest.events.Event

class ScalaTestHtmlReporter extends Reporter {
  private var events: List[Event] = Nil

  override def apply(event: Event): Unit = events = event :: events

  def done(): Unit = writeResult(extract(events))

  private def writeResult(result: Option[SuiteResult]): Unit = result.map(new SingleClairvoyanceHtmlPrinter().print).foreach(new SingleClairvoyanceHtmlFileWriter().writeFile)
}