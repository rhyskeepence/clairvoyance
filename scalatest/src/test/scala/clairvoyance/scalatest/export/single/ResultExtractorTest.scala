package clairvoyance.scalatest.export.single

import clairvoyance.scalatest.export.{ResultExtractor, SuiteResult}
import org.scalatest.FlatSpec
import org.scalatest.events.{Event, NameInfo, ScopeOpened, Ordinal, TestStarting}

import scala.collection.mutable.ListBuffer

class ResultExtractorTest extends FlatSpec {
  "The ResultExtractor" should "be able to extract a result from a " + classOf[TestStarting].getSimpleName in {
    val event: Event = new TestStarting(new Ordinal(0), "suiteName", "suiteId", Some("suiteClassName"), "testName", "testText", None, None, None, None, "Ima Thread", 0L)
    val result: SuiteResult = ResultExtractor.extract(ListBuffer(event), 0).get
    assert(result.suiteId === "suiteId")
    assert(result.suiteName === "suiteName")
    assert(result.suiteClassName === Some("suiteClassName"))
  }

  it should "be able to extract a result from an event that exposes suite details via a nameInfo" in {
    val event: Event = new ScopeOpened(new Ordinal(0), "I work for peanuts", NameInfo("suiteName", "suiteId", Some("suiteClassName"), None), None, None, None, "Ima Thread", 0L)
    val result: SuiteResult = ResultExtractor.extract(ListBuffer(event), 0).get
    assert(result.suiteId === "suiteId")
    assert(result.suiteId === "suiteId")
    assert(result.suiteClassName === Some("suiteClassName"))
  }
}
