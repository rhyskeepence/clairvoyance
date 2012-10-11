package clairvoyance.examples

import org.specs2.clairvoyance.{ClairvoyantContext, ClairvoyantSpec}
import org.specs2.clairvoyance.rendering.CustomRendering

class CustomRenderingExample extends ClairvoyantSpec with CustomRendering {
  "The Custom Renderer" should {
    "be invoked" in new context {
      interestingGivens += ("brain" -> Brain(130))
    }
  }

  trait context extends ClairvoyantContext

  def customRendering = {
    case Brain(iq) => "a Brain with an IQ of %d".format(iq)
  }
}

case class Brain(iq: Int)
