package org.scalatest.tools.clairvoyance

import org.scalatest.events.{Event, SuiteStarting, Summary}
import scala.collection.mutable.ListBuffer

object ScalaTestSpy {

  class SuiteResultHolder {
    val suites = new ListBuffer[SuiteResult]

    def +=(result: SuiteResult): Unit = { suites += result }

    def summary: Summary = {
      val (succeeded, failed, ignored, pending, canceled, scopesPending) = suites.foldLeft((0, 0, 0, 0, 0, 0)) {
        case ((_succeeded, _failed, _ignored, _pending, _canceled, _scopesPending), r) => (
          _succeeded     + r.testsSucceededCount,
          _failed        + r.testsFailedCount,
          _ignored       + r.testsIgnoredCount,
          _pending       + r.testsPendingCount,
          _canceled      + r.testsCanceledCount,
          _scopesPending + r.scopesPendingCount
        )
      }
      Summary(succeeded, failed, ignored, pending, canceled, suites.length, suites.filter(!_.isCompleted).length, scopesPending)
    }

    def totalDuration: Long = suites.map(s => if (s.duration.isDefined) s.duration.get else 0).sum
  }

  case class SuiteResult(
    suiteId: String,
    suiteName: String,
    suiteClassName: Option[String],
    duration: Option[Long],
    startEvent: SuiteStarting,
    endEvent: Event,
    eventList: IndexedSeq[Event],
    testsSucceededCount: Int,
    testsFailedCount:    Int,
    testsIgnoredCount:   Int,
    testsPendingCount:   Int,
    testsCanceledCount:  Int,
    scopesPendingCount:  Int,
    isCompleted: Boolean
  )
}
