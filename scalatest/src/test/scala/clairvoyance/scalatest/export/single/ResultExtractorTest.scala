package clairvoyance.scalatest.export.single

import clairvoyance.scalatest.export.ResultExtractor
import org.scalatest.WordSpec
import org.scalatest.events.{NameInfo, Ordinal, ScopeOpened, TestStarting}

import scala.collection.mutable.ListBuffer

class ResultExtractorTest extends WordSpec {
  def passed = afterWord("passed")

  "ResultExtractor can extract a result" when passed {

    s"a ${classOf[TestStarting].getSimpleName} event" in {
      val event = TestStarting(
        new Ordinal(0),
        "suiteName",
        "suiteId",
        Some("suiteClassName"),
        "testName",
        "testText",
        None,
        None,
        None,
        None,
        "Ima Thread",
        0L
      )
      val result = ResultExtractor.extract(ListBuffer(event), 0).get
      assert(result.suiteId === "suiteId")
      assert(result.suiteName === "suiteName")
      assert(result.suiteClassName === Some("suiteClassName"))
    }

    "an event that exposes suite details via NameInfo" in {
      val event = ScopeOpened(
        new Ordinal(0),
        "I work for peanuts",
        NameInfo("suiteName", "suiteId", Some("suiteClassName"), None),
        None,
        None,
        None,
        "Ima Thread",
        0L
      )
      val result = ResultExtractor.extract(ListBuffer(event), 0).get
      assert(result.suiteId === "suiteId")
      assert(result.suiteId === "suiteId")
      assert(result.suiteClassName === Some("suiteClassName"))
    }
  }
}
