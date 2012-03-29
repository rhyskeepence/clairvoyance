package clairvoyance.plugins

import org.specs2.clairvoyance.plugins.UmlMarkupGeneration
import org.specs2.clairvoyance.Imports._
import org.specs2.clairvoyance.{ProducesCapturedInputsAndOutputs, ClairvoyantSpec}

class SequenceDiagramSpec extends ClairvoyantSpec {

  "The Sequence Diagrammer" should {

    "identify captured values in the format `SOME MESSAGE from X to Y`" in new context {
      givenTheCapturedValuesContains("Banana from Human to Monkey")
      whenTheSequenceDiagramIsGenerated()
      theMarkupGenerated must contain("\"Human\" ->> \"Monkey\":Banana")
    }

    "identify captured values in the format `SOME MESSAGE to Y`, using Donkey Kong as the default" in new context {
      givenTheCapturedValuesContains("Banana to Monkey")
      whenTheSequenceDiagramIsGenerated()
      theMarkupGenerated must contain("\"Donkey Kong\" ->> \"Monkey\":Banana")
    }

    "identify captured values in the format `SOME MESSAGE from Y`, using Donkey Kong as the default" in new context {
      givenTheCapturedValuesContains("Banana from Monkey")
      whenTheSequenceDiagramIsGenerated()
      theMarkupGenerated must contain("\"Monkey\" ->> \"Donkey Kong\":Banana")
    }

    "be case insensititve" in new context {
      givenTheCapturedValuesContains("Banana FROM Human TO Monkey")
      whenTheSequenceDiagramIsGenerated()
      theMarkupGenerated must contain("\"Human\" ->> \"Monkey\":Banana")
    }

    "ignored captured values which do not match the `From/To` format" in new context {
      givenTheCapturedValuesContains("Nonsense")
      whenTheSequenceDiagramIsGenerated()
      theMarkupGenerated must not contain("Nonsense")
    }
  }

  trait context extends ClairvoyantContext with ProducesCapturedInputsAndOutputs{
    override def capturedInputsAndOutputs = Seq(this)

    var capturedValues = Seq[KeyValue]()
    var theMarkupGenerated = ""

    def givenTheCapturedValuesContains(key: String) {
      capturedValues = Seq(key -> "doesn't matter")
    }

    def whenTheSequenceDiagramIsGenerated() {
      theMarkupGenerated = UmlMarkupGeneration.generateUmlMarkup(capturedValues, "Donkey Kong")
      captureValue("Generated UML" -> theMarkupGenerated)
    }
  }
}

