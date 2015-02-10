package clairvoyance.specs2.rendering

import clairvoyance.export.HumanisedCodeFormat
import clairvoyance.specs2.ClairvoyantSpec

class HumanisedCodeSpec extends ClairvoyantSpec with HumanisedCodeFormat {
  
  "Code Format" should {
    "be overriden" in {
      givenOneThing()
      givenAnotherThing()
      whenIOpenMyEyes()
      thenI() must seeSomething
    }
  }
  
  def givenOneThing(): Unit = ()
  def givenAnotherThing(): Unit = ()
  def whenIOpenMyEyes(): Unit = ()
  def thenI(): Unit = ()
  def seeSomething = be_===(())

}
