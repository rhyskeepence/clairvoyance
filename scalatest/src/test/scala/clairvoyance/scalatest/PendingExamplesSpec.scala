package clairvoyance.scalatest

import org.scalatest.{Ignore, MustMatchers, Spec}

class PendingExamplesSpec extends Spec with MustMatchers {

  object `In ScalaTest, examples can be marked as` {
    @Ignore
    def `ignored with the @Ignore annotation`() {
      fail("how do I know I'm done?")
    }
    def `pending by returning pending` = pending
    def `pending by a one-line block returning pending`() { pending }
    def `pending by a multi-line block returning pending`() {
      pending
    }
    def `pending by returning a one-line block returning pending` = { pending }
    def `pending by returning a multi-line block returning pending` = {
      pending
    }
    def `pending until fixed`() {
      pendingUntilFixed {
        1 + 2 mustEqual 4
      }
    }
  }
}
