package clairvoyance.scalatest.examples

import clairvoyance.scalatest.ClairvoyantContext
import org.scalatest.FunSuite

class HtmlReportWithoutTestOptionExample extends FunSuite with ClairvoyantContext {
  test("This test can be run on it's own, without providing the -C test option, and an html report will still be created") {
    assert("Istanbul" !== "Constantinople")
  }
}