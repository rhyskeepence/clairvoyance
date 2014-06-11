package clairvoyance.scalatest

import org.scalatest.{MustMatchers, Spec}

class FailedExamplesSpec extends Spec with MustMatchers {

  object `In ScalaTest, examples can fail` {
    def `without a message`() {
      1 + 2 mustEqual "foo"
    }
    def `by throwing an exception`() {
      throw new IllegalArgumentException
    }
    def `with a message`() {
      fail("why bother?")
    }
    def `with an exception`() {
      fail(new IllegalArgumentException)
    }
    def `with a message and an exception`() {
      fail("why bother?", new IllegalArgumentException)
    }
  }
}
