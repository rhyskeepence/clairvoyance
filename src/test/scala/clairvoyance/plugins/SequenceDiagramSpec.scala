package clairvoyance.plugins

import org.specs2.clairvoyance.Imports._
import org.specs2.clairvoyance.{CapturedValue, ClairvoyantContext, ProducesCapturedInputsAndOutputs, ClairvoyantSpec}
import org.specs2.clairvoyance.plugins.CapturedCollaborators._
import org.specs2.clairvoyance.plugins.UmlMarkupGeneration._

class SequenceDiagramSpec extends ClairvoyantSpec {

  "The Sequence Diagrammer" should {

    "identify captured values in the format `SOME MESSAGE from X to Y`" in new context {
      givenTheCapturedValuesContains("Banana from Human to Monkey")
      whenTheSequenceDiagramIsGenerated()
      theMarkupGenerated must contain("\"Human\" ->> \"Monkey\":<text class=sequence_diagram_clickable sequence_diagram_message_id=1>Banana</text>")
    }

    "identify captured values in the format `SOME MESSAGE to Y`, using Donkey Kong as the default" in new context {
      givenTheCapturedValuesContains("Banana to Monkey")
      whenTheSequenceDiagramIsGenerated()
      theMarkupGenerated must contain("\"Donkey Kong\" ->> \"Monkey\":<text class=sequence_diagram_clickable sequence_diagram_message_id=1>Banana</text>")
    }

    "identify captured values in the format `SOME MESSAGE from Y`, using Donkey Kong as the default" in new context {
      givenTheCapturedValuesContains("Banana from Monkey")
      whenTheSequenceDiagramIsGenerated()
      theMarkupGenerated must contain("\"Monkey\" ->> \"Donkey Kong\":<text class=sequence_diagram_clickable sequence_diagram_message_id=1>Banana</text>")
    }

    "be case insensititve" in new context {
      givenTheCapturedValuesContains("Banana FROM Human TO Monkey")
      whenTheSequenceDiagramIsGenerated()
      theMarkupGenerated must contain("\"Human\" ->> \"Monkey\":<text class=sequence_diagram_clickable sequence_diagram_message_id=1>Banana</text>")
    }

    "ignored captured values which do not match the `From/To` format" in new context {
      givenTheCapturedValuesContains("Nonsense")
      whenTheSequenceDiagramIsGenerated()
      theMarkupGenerated must not contain("Nonsense")
    }
  }

  trait context extends ClairvoyantContext with ProducesCapturedInputsAndOutputs{
    override def capturedInputsAndOutputs = Seq(this)

    var capturedValues = Seq[CapturedValue]()
    var theMarkupGenerated = ""

    def givenTheCapturedValuesContains(key: String) {
      capturedValues = Seq(CapturedValue(1, key, "doesn't matter"))
    }

    def whenTheSequenceDiagramIsGenerated() {
      theMarkupGenerated = generateUmlMarkup(collectCollaborators(capturedValues, "Donkey Kong"))
      captureValue("Generated UML" -> theMarkupGenerated)
    }
  }
}

