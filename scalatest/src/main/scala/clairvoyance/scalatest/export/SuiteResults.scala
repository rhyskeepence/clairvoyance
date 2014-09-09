package clairvoyance.scalatest.export

import org.scalatest.events.{Event, SuiteStarting}
import scala.collection.mutable.ListBuffer

case class SuiteResult(suiteId: String,
                       suiteName: String,
                       suiteClassName: Option[String],
                       duration: Option[Long],
                       eventList: IndexedSeq[Event],
                       testsPassedCount: Int,
                       testsFailedCount: Int,
                       testsIgnoredCount: Int,
                       testsPendingCount: Int,
                       testsCancelledCount: Int,
                       scopesPendingCount: Int,
                       isCompleted: Boolean)

private[export]
class SuiteResults {
  private val results = new ListBuffer[SuiteResult]

  def suites: Seq[SuiteResult] = results.toSeq

  def +=(result: SuiteResult): Unit = { results += result }

  def totalDuration: Long = results.map(_.duration.fold(0L)(duration => duration)).sum
}
