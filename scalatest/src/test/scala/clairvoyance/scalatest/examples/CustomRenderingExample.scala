package clairvoyance.scalatest.examples

import clairvoyance.rendering.CustomRendering
import clairvoyance.scalatest.ClairvoyantContext
import org.scalatest.WordSpec

class CustomRenderingExample extends WordSpec with ClairvoyantContext with CustomRendering {

  "The Custom Renderer" should {
    "be invoked" in {
      interestingGivens += ("brain" -> Brain(130))
    }
  }

  def customRendering = { case Brain(iq) => s"a Brain with an IQ of $iq" }
}

case class Brain(iq: Int)
