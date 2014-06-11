package clairvoyance.scalatest

import org.scalatest.Spec

class CancelledExamplesSpec extends Spec {

  object `In ScalaTest, examples can be cancelled` {
    def `by an unmet condition`() {
      assume(false)
    }
    def `by an unmet condition with a clue as to why it was not met`() {
      assume(false, "external resource not available")
    }
    def `explicitly`() {
      cancel()
    }
    def `explicitly with a message`() {
      cancel("just give up")
    }
    def `explicitly with an exception`() {
      cancel(new IllegalArgumentException)
    }
    def `explicitly with a message and an exception`() {
      cancel("Bobby P.", new IllegalArgumentException)
    }
  }
}
