package clairvoyance.scalatest.examples

import clairvoyance.scalatest.{Clairvoyance, ClairvoyantContext}
import org.scalatest.{Spec, Suite}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class ScalaCheckExample extends Spec with Clairvoyance with ScalaCheckContext {

  object `Strings must` {
    def `support startsWith` {
      givenTwoStrings((a: String, b: String) =>
        (a + b).startsWith(a) mustBe true)
  }

    def `support endsWith` {
      givenTwoStrings((a: String, b: String) =>
        (a + b).endsWith(b) mustBe true)
    }

    def `support concat` {
      givenTwoStrings((a: String, b: String) =>
        (a + b).length mustEqual (a.length + b.length))
    }

    def `support substring` {
      givenTwoStrings((a: String, b: String) =>
        (a + b).substring(a.length) mustEqual b)
    }
  }
}

trait ScalaCheckContext extends ClairvoyantContext with GeneratorDrivenPropertyChecks { this: Suite =>
  def givenTwoStrings(f: (String, String) => Unit) = forAll { f }
}
