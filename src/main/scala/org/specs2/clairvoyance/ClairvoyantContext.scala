package org.specs2.clairvoyance

import org.specs2.mutable.After
import org.specs2.clairvoyance.state.{TestState, TestStates}

trait ClairvoyantContext extends After with InterestingGivens with CapturedInputsAndOutputs {

  def tearDown {}

  def after {
    tearDown

    TestStates += (this -> TestState(interestingGivens.toList, gatherCapturedValues))
    clearCapturedValues()
  }


  implicit def stringToStep(description: String) = new ClairvoyantStep(description)
  class ClairvoyantStep(description: String) {
    def ==>[T](step: T): T = step
  }
}

