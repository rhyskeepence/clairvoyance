package clairvoyance.scalatest.export

import org.scalatest.events.{Event, NameInfo, ScopePending, TestCanceled, TestFailed, TestIgnored, TestPending, TestSucceeded}

import scala.collection.mutable.ListBuffer
import scala.util.Try

object ResultExtractor {
  private type HasNameInfo = {def nameInfo: NameInfo}
  private def hasNameInfo(e: Event): Boolean = hasMethod(e, "nameInfo", classOf[NameInfo])

  private type HasSuiteDetails = {def suiteId: String; def suiteName: String; def suiteClassName: Option[String]}
  private def hasSuiteDetails(e: Event): Boolean =
    hasMethod(e, "suiteName", classOf[String]) &&
    hasMethod(e, "suiteId", classOf[String]) &&
    hasMethod(e, "suiteClassName", classOf[Option[String]])

  def extract(events: ListBuffer[Event], durationInMillis: Long): Option[SuiteResult] =
    events.collectFirst {
      case e if hasNameInfo(e) => e.asInstanceOf[HasNameInfo].nameInfo
      case e if hasSuiteDetails(e) => e.asInstanceOf[HasSuiteDetails]
    }.map { suiteDetails =>
      SuiteResult(suiteDetails.suiteId, suiteDetails.suiteName, suiteDetails.suiteClassName, Some(durationInMillis), events.toIndexedSeq,
        events.count(_.isInstanceOf[TestSucceeded]),
        events.count(_.isInstanceOf[TestFailed]),
        events.count(_.isInstanceOf[TestIgnored]),
        events.count(_.isInstanceOf[TestPending]),
        events.count(_.isInstanceOf[TestCanceled]),
        events.count(_.isInstanceOf[ScopePending]), isCompleted = true)
    }

  private def hasMethod(e: Event, name: String, returnType: Class[_]): Boolean = Try(e.getClass.getMethod(name)).toOption.exists(_.getReturnType == returnType)
}
