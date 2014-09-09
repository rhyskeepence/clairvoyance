package clairvoyance.scalatest.export

import clairvoyance.export.ClairvoyanceHtmlFileWriter
import org.scalatest.ResourcefulReporter
import org.scalatest.events._
import scala.collection.mutable.ListBuffer
import scala.util.Properties.{propOrElse, userDir}

/** required by IDEA's ScalaTest runner only */
class ScalaTestHtmlReporterWithLocation extends ScalaTestHtmlReporter

class ScalaTestHtmlReporter extends ResourcefulReporter with ClairvoyanceHtmlPrinter with ClairvoyanceHtmlFileWriter {
  type ExportType = Unit

  private val results   = new SuiteResults
  private var eventList = new ListBuffer[Event]
  private var runEndEvent: Option[Event] = None

  def apply(event: Event): Unit = event match {
    case _: DiscoveryStarting  =>
    case _: DiscoveryCompleted =>
    case _: RunStarting        =>
    case _: RunCompleted       => runEndEvent = Some(event)
    case _: RunStopped         => runEndEvent = Some(event)
    case _: RunAborted         => runEndEvent = Some(event)
    case e: SuiteCompleted     => addSuiteResult(e, e.suiteName, e.suiteId, e.suiteClassName, e.duration, completed = true)
    case e: SuiteAborted       => addSuiteResult(e, e.suiteName, e.suiteId, e.suiteClassName, e.duration, completed = false)
    case _ => eventList += event
  }

  private def addSuiteResult(event: Event, suiteName: String, suiteId: String, suiteClassName: Option[String], duration: Option[Long], completed: Boolean): Unit = {
    val (suiteEvents, otherEvents) = extractSuiteEvents(suiteId)
    eventList = otherEvents

    val sortedSuiteEvents = suiteEvents.sorted
    if (sortedSuiteEvents.length == 0)
      throw new IllegalStateException(
        s"Expected SuiteStarting for completion event: $event in the head of suite events, but we got no suite event at all")

    sortedSuiteEvents.head match {
      case suiteStarting: SuiteStarting =>
        val accumulatedResult = SuiteResult(
          suiteId, suiteName, suiteClassName, duration, Vector.empty ++ sortedSuiteEvents.tail,
          testsPassedCount    = 0, testsFailedCount   = 0, testsIgnoredCount = 0, testsPendingCount = 0,
          testsCancelledCount = 0, scopesPendingCount = 0, isCompleted = completed)

        val suiteResult = sortedSuiteEvents.foldLeft(accumulatedResult) {
          case (r, e) => e match {
            case _: TestSucceeded => r.copy(testsPassedCount    = r.testsPassedCount     + 1)
            case _: TestFailed    => r.copy(testsFailedCount    = r.testsFailedCount     + 1)
            case _: TestIgnored   => r.copy(testsIgnoredCount   = r.testsIgnoredCount    + 1)
            case _: TestPending   => r.copy(testsPendingCount   = r.testsPendingCount    + 1)
            case _: TestCanceled  => r.copy(testsCancelledCount = r.testsCancelledCount  + 1)
            case _: ScopePending  => r.copy(scopesPendingCount  = r.scopesPendingCount   + 1)
            case _ => r
          }
        }
        if (!completed || sortedSuiteEvents.head.asInstanceOf[SuiteStarting].formatter != Some(MotionToSuppress)) {
          results += suiteResult
        }
      case other => throw new IllegalStateException(
        s"Expected SuiteStarting for completion event: $event in the head of suite events, but we got: $other")
    }
  }

  private def extractSuiteEvents(suiteId: String): (ListBuffer[Event], ListBuffer[Event]) = {
    def relatedToSuite(nameInfo: Option[NameInfo]): Boolean = nameInfo.fold(false)(_.suiteId == suiteId)
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

  protected def allSuiteResults: Seq[SuiteResult] = {
    val (specs, examples) = results.suites.sortBy(_.suiteName).toSeq.partition(_.suiteName endsWith "Spec")
    examples ++ specs
  }

  protected def outputDir = propOrElse("scalatest.output.dir", s"$userDir/target/clairvoyance-reports/")

  private def writeResults(resourceName: String, duration: Option[Long]): Unit = writeFiles(results.suites.map(print))
}
