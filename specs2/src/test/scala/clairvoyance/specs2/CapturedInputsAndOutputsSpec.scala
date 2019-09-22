package clairvoyance.specs2

import clairvoyance.ProducesCapturedInputsAndOutputs

class CapturedInputsAndOutputsSpec extends ClairvoyantSpec {

  "The Captured Inputs And Outputs" should {
    "include outputs from registered producers" in new context {
      whenFootieIsStarting()
      thenTheCapturedValueNamed("Bazza says") mustEqual "Oi Shazza, get me a beer, the game's starting."
    }
  }

  trait context extends ClairvoyantContext {
    val bazza                             = new Bazza
    override def capturedInputsAndOutputs = Seq(bazza)

    def whenFootieIsStarting(): Unit = { bazza.footieIsStarting() }

    def thenTheCapturedValueNamed(key: String) =
      gatherCapturedValues
        .map(_.toPair)
        .toMap
        .apply(key)
  }

  class Bazza extends ProducesCapturedInputsAndOutputs {
    def footieIsStarting(): Unit = {
      captureValue("Bazza says" -> "Oi Shazza, get me a beer, the game's starting.")
    }
  }
}
