package clairvoyance.examples

import org.specs2.clairvoyance.{ProducesCapturedInputsAndOutputs, ClairvoyantSpec}
import org.specs2.clairvoyance.plugins.SequenceDiagram


class SequenceDiagramExample extends ClairvoyantSpec {

  "Mario" should {
    "giveDaisyTo Daisy to Luigi" in new context {
      mario.giveDaisyTo(luigy)
    }
  }

  trait context extends ClairvoyantContext with SequenceDiagram {
    val mario = new Mario
    val luigy = {}
  }

  class Mario extends ProducesCapturedInputsAndOutputs {
    def giveDaisyTo(any: Any) {
      captureValue("Daisy from Mario to Luigy" -> "")
    }
  }

}
