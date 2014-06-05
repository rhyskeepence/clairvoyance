package clairvoyance.scalatest.export

import clairvoyance.export.ClairvoyanceHtmlFileWriter
import org.scalatest.ResourcefulReporter
import org.scalatest.tools.clairvoyance.ScalaTestSpy.{SuiteResultHolder, SuiteResult}
import org.scalatest.events._
import scala.collection.mutable.ListBuffer
import scalaz.Scalaz.ToIdOps

/** required by IDEA's ScalaTest runner only */
class ScalaTestHtmlReporterWithLocation extends ScalaTestHtmlReporter

class ScalaTestHtmlReporter extends ResourcefulReporter with ClairvoyanceHtmlPrinter with ClairvoyanceHtmlFileWriter {
  type ExportType = Unit

  private val results   = new SuiteResultHolder
  private var eventList = new ListBuffer[Event]
  private var runEndEvent: Option[Event] = None

  def apply(event: Event): Unit = {
    event match {
      case _: DiscoveryStarting  =>
      case _: DiscoveryCompleted =>
      case _: RunStarting  =>
      case _: RunCompleted => runEndEvent = Some(event)
      case _: RunStopped   => runEndEvent = Some(event)
      case _: RunAborted   => runEndEvent = Some(event)

      case SuiteCompleted(ordinal, suiteName, suiteId, suiteClassName, duration, formatter, location, rerunner, payload, threadName, timeStamp) =>
        val (suiteEvents, otherEvents) = extractSuiteEvents(suiteId)
        eventList = otherEvents

        val sortedSuiteEvents = suiteEvents.sorted
        if (sortedSuiteEvents.length == 0)
          throw new IllegalStateException(
            s"Expected SuiteStarting for completion event: $event in the head of suite events, but we got no suite event at all")

        sortedSuiteEvents.head match {
          case suiteStarting: SuiteStarting =>
            val accumulatedResult = SuiteResult(
              suiteId, suiteName, suiteClassName, duration, suiteStarting, event, Vector.empty ++ sortedSuiteEvents.tail,
              testsSucceededCount = 0, testsFailedCount   = 0, testsIgnoredCount = 0, testsPendingCount = 0,
              testsCanceledCount  = 0, scopesPendingCount = 0, isCompleted = true)

            val suiteResult = sortedSuiteEvents.foldLeft(accumulatedResult) {
              case (r, e) => e match {
                case _: TestSucceeded => r.copy(testsSucceededCount = r.testsSucceededCount + 1)
                case _: TestFailed    => r.copy(testsFailedCount    = r.testsFailedCount    + 1)
                case _: TestIgnored   => r.copy(testsIgnoredCount   = r.testsIgnoredCount   + 1)
                case _: TestPending   => r.copy(testsPendingCount   = r.testsPendingCount   + 1)
                case _: TestCanceled  => r.copy(testsCanceledCount  = r.testsCanceledCount  + 1)
                case _: ScopePending  => r.copy(scopesPendingCount  = r.scopesPendingCount  + 1)
                case _ => r
              }
            }

            val suiteStartingEvent = sortedSuiteEvents.head.asInstanceOf[SuiteStarting]
            if (suiteStartingEvent.formatter != Some(MotionToSuppress)) {
              results += suiteResult
            }
          case other => throw new IllegalStateException(
            s"Expected SuiteStarting for completion event: $event in the head of suite events, but we got: $other")
        }

      case SuiteAborted(_, _, suiteName, suiteId, suiteClassName, _, duration, _, _, _, _, _, _) =>
        val (suiteEvents, otherEvents) = extractSuiteEvents(suiteId)
        eventList = otherEvents

        val sortedSuiteEvents = suiteEvents.sorted
        if (sortedSuiteEvents.length == 0)
          throw new IllegalStateException(
            s"Expected SuiteStarting for completion event: $event in the head of suite events, but we got no suite event at all")

        sortedSuiteEvents.head match {
          case suiteStarting: SuiteStarting =>
            val accumulatedResult = SuiteResult(
              suiteId, suiteName, suiteClassName, duration, suiteStarting, event, Vector.empty ++ sortedSuiteEvents.tail,
              testsSucceededCount = 0, testsFailedCount   = 0, testsIgnoredCount = 0, testsPendingCount = 0,
              testsCanceledCount  = 0, scopesPendingCount = 0, isCompleted = false)

            val suiteResult = sortedSuiteEvents.foldLeft(accumulatedResult) {
              case (r, e) => e match {
                case _: TestSucceeded => r.copy(testsSucceededCount = r.testsSucceededCount + 1)
                case _: TestFailed    => r.copy(testsFailedCount    = r.testsFailedCount    + 1)
                case _: TestIgnored   => r.copy(testsIgnoredCount   = r.testsIgnoredCount   + 1)
                case _: TestPending   => r.copy(testsPendingCount   = r.testsPendingCount   + 1)
                case _: TestCanceled  => r.copy(testsCanceledCount  = r.testsCanceledCount  + 1)
                case _: ScopePending  => r.copy(scopesPendingCount  = r.scopesPendingCount  + 1)
                case _ => r
              }
            }
            results += suiteResult
          case other => throw new IllegalStateException(
            s"Expected SuiteStarting for completion event: $event in the head of suite events, but we got: $other")
        }
      case _ => eventList += event
    }
  }

  private def extractSuiteEvents(suiteId: String): (ListBuffer[Event], ListBuffer[Event]) = {
    def relatedToSuite(nameInfoOption: Option[NameInfo]): Boolean = nameInfoOption match {
      case Some(nameInfo) => nameInfo.suiteId == suiteId
      case None => false
    }
    eventList partition {
      case e: TestStarting   => e.suiteId == suiteId
      case e: TestSucceeded  => e.suiteId == suiteId
      case e: TestIgnored    => e.suiteId == suiteId
      case e: TestFailed     => e.suiteId == suiteId
      case e: TestPending    => e.suiteId == suiteId
      case e: TestCanceled   => e.suiteId == suiteId
      case e: InfoProvided   => relatedToSuite(e.nameInfo)
      case e: AlertProvided  => relatedToSuite(e.nameInfo)
      case e: NoteProvided   => relatedToSuite(e.nameInfo)
      case e: MarkupProvided => relatedToSuite(e.nameInfo)
      case e: ScopeOpened    => e.nameInfo.suiteId == suiteId
      case e: ScopeClosed    => e.nameInfo.suiteId == suiteId
      case e: ScopePending   => e.nameInfo.suiteId == suiteId
      case e: SuiteStarting  => e.suiteId == suiteId
      case _ => false
    }
  }

  def dispose(): Unit = runEndEvent match {
    case Some(event) => event match {
      case e: RunCompleted => writeResults("runCompleted", e.duration)
      case e: RunStopped   => writeResults("runStopped",   e.duration)
      case e: RunAborted   => writeResults("runAborted",   e.duration)
      case other => throw new IllegalStateException(s"Expected run ending event only, but got: ${other.getClass.getName}")
    }
    // if no run end event (like when run in sbt), just use 'runCompleted' with the sum of the suites' duration
    case None => writeResults("runCompleted", Some(results.totalDuration))
  }

  def allSuiteResults: Seq[SuiteResult] = results.suites.toSeq

  private def writeResults(resourceName: String, duration: Option[Long]): Unit = results.suites.map(print) |> writeFiles
}
