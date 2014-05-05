package clairvoyance.examples

import clairvoyance.{ClairvoyantContext, ClairvoyantSpec}
import clairvoyance.rendering.CustomRendering

class CustomRenderingExample extends ClairvoyantSpec with CustomRendering {
  "The Custom Renderer" should {
    "be invoked" in new context {
      interestingGivens += ("brain" -> Brain(130))
    }
  }

  trait context extends ClairvoyantContext

  def customRendering = {
    case Brain(iq) => s"a Brain with an IQ of $iq"
  }
}

case class Brain(iq: Int)
