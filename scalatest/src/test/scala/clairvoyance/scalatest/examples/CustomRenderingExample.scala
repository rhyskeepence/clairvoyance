package clairvoyance.scalatest.examples

import clairvoyance.rendering.CustomRendering
import clairvoyance.scalatest.ClairvoyantContext
import org.scalatest.Spec

class CustomRenderingExample extends Spec with ClairvoyantContext with CustomRendering {

  object `The Custom Renderer should` {
    def `be invoked`() {
      interestingGivens += ("brain" -> Brain(130))
    }
  }

  def customRendering = { case Brain(iq) => s"a Brain with an IQ of $iq" }
}

case class Brain(iq: Int)
