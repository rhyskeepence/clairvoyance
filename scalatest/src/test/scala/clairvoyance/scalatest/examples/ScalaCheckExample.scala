package clairvoyance.scalatest.examples

import clairvoyance.scalatest.ClairvoyantContext
import org.scalatest.{MustMatchers, Suite, WordSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class ScalaCheckExample extends WordSpec with ScalaCheckContext with MustMatchers {
  def support = afterWord("support")

  "String" must support {
    "startsWith" in {
      givenTwoStrings((a: String, b: String) =>
        (a + b).startsWith(a) mustBe true)
    }

    "String.endsWith" in {
      givenTwoStrings((a: String, b: String) =>
        (a + b).endsWith(b) mustBe true)
    }

    "String.concat" in {
      givenTwoStrings((a: String, b: String) =>
        (a + b).length mustEqual (a.length + b.length))
    }

    "String.substring" in {
      givenTwoStrings((a: String, b: String) =>
        (a + b).substring(a.length) mustEqual b)
    }
  }
}

trait ScalaCheckContext extends ClairvoyantContext with GeneratorDrivenPropertyChecks { this: Suite =>
  def givenTwoStrings(f: (String, String) => Unit) = forAll { f }
}
