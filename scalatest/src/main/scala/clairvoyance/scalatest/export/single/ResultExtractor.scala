package clairvoyance.scalatest.export.single

import clairvoyance.scalatest.export.SuiteResult
import org.scalatest.events.{ScopePending, TestCanceled, TestPending, TestIgnored, TestFailed, TestSucceeded, Event, NameInfo}

import scala.util.Try

object ResultExtractor {
  private type HasNameInfo = {def nameInfo: NameInfo}

  def extract(events: List[Event]): Option[SuiteResult] =
    events.find(isNameInfoable).map(_.asInstanceOf[HasNameInfo]).map { ni =>
      val details: (String, String, Option[String]) = suiteDetails(ni)
      SuiteResult(details._1, details._2, details._3, None, events.toIndexedSeq,
        events.count(_.isInstanceOf[TestSucceeded]),
        events.count(_.isInstanceOf[TestFailed]),
        events.count(_.isInstanceOf[TestIgnored]),
        events.count(_.isInstanceOf[TestPending]),
        events.count(_.isInstanceOf[TestCanceled]),
        events.count(_.isInstanceOf[ScopePending]), true)
    }


  private def isNameInfoable(e: Event): Boolean = Try(e.getClass.getMethod("nameInfo")).toOption.exists(_.getReturnType == classOf[NameInfo])

  private def suiteDetails(event: HasNameInfo): (String, String, Option[String]) = (event.nameInfo.suiteId, event.nameInfo.suiteName, event.nameInfo.suiteClassName)
}
