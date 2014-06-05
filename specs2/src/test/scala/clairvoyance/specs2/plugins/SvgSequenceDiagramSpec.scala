package clairvoyance.specs2.plugins

import clairvoyance.ProducesCapturedInputsAndOutputs
import clairvoyance.plugins.{CapturedValueCollaborators, SvgSequenceDiagram}
import clairvoyance.specs2.{ClairvoyantSpec, ClairvoyantContext}

class SvgSequenceDiagramSpec extends ClairvoyantSpec {

  "The Rendered SVG Sequence Diagram" should {

    "contain actor named Bees" in new context {
      svgMarkupProduced must =~("<text.*>Bees</text")
    }

    "contain actor named Human" in new context {
      svgMarkupProduced must =~("<text.*>Human</text>")
    }

    "contain actor named Robots" in new context {
      svgMarkupProduced must =~("<text.*>Robots</text>")
    }

    "contain clickable message" in new context {
      svgMarkupProduced must =~(".*<text.*class=\"sequence_diagram_clickable\".*sequence_diagram_message_id=\"1\" .*>Honey</text>.*")
    }
  }

  trait context extends ClairvoyantContext with ProducesCapturedInputsAndOutputs {
    override def capturedInputsAndOutputs = Seq(this)

    val collaborators = Seq(
      CapturedValueCollaborators(1, "Honey from Bees to Human", "Bees", "Human", "Honey", "raw value"),
      CapturedValueCollaborators(2, "Binary from Robots to Human", "Robots", "Human", "Binary", "0001101011")
    )

    def svgMarkupProduced = {
      val svgOutput = SvgSequenceDiagram(collaborators).toMarkup
      captureValue("SVG Markup produced" -> svgOutput)
      svgOutput
    }
  }
}
