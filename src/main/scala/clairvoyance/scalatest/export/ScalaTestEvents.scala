package clairvoyance.scalatest.export

import org.scalatest.events._

case class ScopeOpenedOrPending(nameInfo: NameInfo, formatter: Option[Formatter], resourceName: String)

object ScopeOpenedOrPending {
  def unapply(event: Event): Option[ScopeOpenedOrPending] = event match {
    case ScopeOpened (_, _, nameInfo, formatter, _, _, _, _) => Some(ScopeOpenedOrPending(nameInfo, formatter, "scopeOpened"))
    case ScopePending(_, _, nameInfo, formatter, _, _, _, _) => Some(ScopeOpenedOrPending(nameInfo, formatter, "scopePending"))
    case _ => None
  }
}

trait ScalaTestEvent {
  def testName: String
  def testText: String
  def recordedEvents: IndexedSeq[RecordableEvent]
  def cssClass: String
}

case class TestFailedOrCancelled(message: String,
                                 suiteClassName: Option[String],
                                 testName: String,
                                 testText: String,
                                 recordedEvents: IndexedSeq[RecordableEvent],
                                 throwable: Option[Throwable],
                                 duration: Option[Long],
                                 failed: Boolean)
  extends ScalaTestEvent {
  def name     = if (failed) "Failed"      else "Cancelled"
  def cssClass = if (failed) "test_failed" else "test_canceled"
}

object TestFailedOrCancelled {
  def unapply(event: Event): Option[TestFailedOrCancelled] = event match {
    case TestFailed  (_, message, _, _, suiteClassName, testName, testText, recordedEvents, throwable, duration, _, _, _, _, _, _) => Some(TestFailedOrCancelled(message, suiteClassName, testName, testText, recordedEvents, throwable, duration, failed = true))
    case TestCanceled(_, message, _, _, suiteClassName, testName, testText, recordedEvents, throwable, duration, _, _, _, _, _, _) => Some(TestFailedOrCancelled(message, suiteClassName, testName, testText, recordedEvents, throwable, duration, failed = false))
    case _ => None
  }
}

case class TestPendingOrIgnored(suiteClassName: Option[String],
                                testName: String,
                                testText: String,
                                recordedEvents: IndexedSeq[RecordableEvent],
                                pending: Boolean)
  extends ScalaTestEvent {
  def name     = if (pending) "Pending"      else "Ignored"
  def cssClass = if (pending) "test_pending" else "test_ignored"
}

object TestPendingOrIgnored {
  def unapply(event: Event): Option[TestPendingOrIgnored] = event match {
    case TestPending(_, _, _, suiteClassName, testName, testText, recordedEvents, _, _, _, _, _, _) => Some(TestPendingOrIgnored(suiteClassName, testName, testText, recordedEvents, pending = true))
    case TestIgnored(_, _, _, suiteClassName, testName, testText, _, _, _, _, _) => Some(TestPendingOrIgnored(suiteClassName, testName, testText, IndexedSeq.empty, pending = false))
    case _ => None
  }
}
