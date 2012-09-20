package clairvoyance.tests

import org.specs2.clairvoyance.ClairvoyantSpec
import org.specs2.clairvoyance.plugins.SvgSequenceDiagram

class SvgSequenceDiagramSpec extends ClairvoyantSpec {

  "The SVG Sequence Diagram" should {

    "render to SVG Markup" in new context {
      diagram.toMarkup must contain("<text")
    }

  }

  trait context extends ClairvoyantContext {
    val umlMarkup = """
@startuml
"Bees" ->> "Human":"Honey"
"Robots" ->> "Human":"0001101011"
@enduml
                    """

    val diagram = SvgSequenceDiagram(umlMarkup)

    interestingGivens += "UML Markup" -> umlMarkup

  }

}
