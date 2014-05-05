package clairvoyance.rendering

import clairvoyance.ProducesCapturedInputsAndOutputs
import clairvoyance.specs2.{ClairvoyantSpec, ClairvoyantContext}
import scala.xml.PrettyPrinter

class RenderingSpec extends ClairvoyantSpec {

  "The Renderer" should {
    "render objects as strings" in new context {
      rendered(brain) must_== <span>{brain.toString}</span>
    }

    "render XML as escaped XML" in new context {
      rendered(xml) must_== <span>{formattedXml}</span>
    }

    "use the custom renderer" in new context {
      rendered(ToBeCustomRendered("nine times")) must_== <span>nine times</span>
    }
  }

  trait context extends ClairvoyantContext with ProducesCapturedInputsAndOutputs  {
    val rendering = new Rendering(Some(new CustomRenderer))
    val brain = Brain(120)

    override def capturedInputsAndOutputs = Seq(this)

    val xml = <xml>someXml</xml>
    val formattedXml = new PrettyPrinter(80, 2).formatNodes(xml)

    def rendered(thingToRender: AnyRef) = {
      captureValue("Input" -> thingToRender)
      val output = rendering.renderToXml(thingToRender)
      captureValue("Output" -> output)
      output
    }
  }

  case class Brain(iq: Int)

  case class ToBeCustomRendered(stringToRender: String)

  class CustomRenderer extends CustomRendering {
    def customRendering = {
      case ToBeCustomRendered(x) => x
    }
  }
}
