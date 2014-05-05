package clairvoyance

class InterestingGivensSpec extends ClairvoyantSpec {
  "The Interesting Givens list" should {

    "include default Interesting Givens" in new context {
      interestingGivens("Name") should beSetTo("Bazza")
    }

    "include both default and context specific Interesting Givens" in new context {
      interestingGivens += ("Spouse" -> "Shazza")

      interestingGivens("Name") should beSetTo("Bazza")
      interestingGivens("Spouse") should beSetTo("Shazza")
    }

    "not include Interesting Givens from the previous context" in new context {
      interestingGivens("Spouse") should notBeSet
    }
  }

  trait context extends ClairvoyantContext {
    ("Name" -> "Bazza").isInteresting()
  }

  def beSetTo[T](t: =>T) = beSome(t)
  def notBeSet = beNone
}
