package clairvoyance.specs2.examples

import clairvoyance.specs2.{ClairvoyantSpec, ClairvoyantContext}
import org.scalacheck.Prop.forAll
import org.specs2.ScalaCheck

class ScalaCheckExample extends ClairvoyantSpec with ScalaCheck {

  "String" should {
    "support startsWith" in new context {
      givenTwoStrings((a: String, b: String) => (a + b).startsWith(a) must beTrue)
    }

    "support endsWith" in new context {
      givenTwoStrings((a: String, b: String) => (a + b).endsWith(b) must beTrue)
    }

    "support concat" in new context {
      givenTwoStrings((a: String, b: String) => (a + b).length must beEqualTo(a.length + b.length))
    }

    "support substring" in new context {
      givenTwoStrings((a: String, b: String) => (a + b).substring(a.length) must beEqualTo(b))
    }
  }

  trait context extends ClairvoyantContext {
    def givenTwoStrings(f: (String, String) => Boolean) = check(forAll { f })
  }
}
