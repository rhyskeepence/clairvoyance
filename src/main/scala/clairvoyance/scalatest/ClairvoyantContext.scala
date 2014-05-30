package clairvoyance.scalatest

import clairvoyance.{CapturedInputsAndOutputs, InterestingGivens}
import clairvoyance.state.{TestState, TestStates}
import org.scalatest.{Suite, BeforeAndAfterEach}

trait ClairvoyantContext extends BeforeAndAfterEach with InterestingGivens with CapturedInputsAndOutputs {
  this: Suite =>

  override protected def afterEach(): Unit = {
    tearDown()

    TestStates += (this -> TestState(interestingGivens.toList, gatherCapturedValues))
    clearCapturedValues()
  }

  protected def tearDown(): Unit = ()

  implicit def stringToStep(description: String) = new ClairvoyantStep(description)
  class ClairvoyantStep(description: String) {
    def ===>[T](step: T): T = step
  }
}
