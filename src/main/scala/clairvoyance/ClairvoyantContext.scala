package clairvoyance

import clairvoyance.state.{TestState, TestStates}
import org.specs2.mutable.After

trait ClairvoyantContext extends After with InterestingGivens with CapturedInputsAndOutputs {

  def tearDown(): Unit = {}

  def after: Unit = {
    tearDown()

    TestStates += (this -> TestState(interestingGivens.toList, gatherCapturedValues))
    clearCapturedValues()
  }


  implicit def stringToStep(description: String) = new ClairvoyantStep(description)
  class ClairvoyantStep(description: String) {
    def ===>[T](step: T): T = step
  }
}
