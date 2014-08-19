package clairvoyance.scalatest

import clairvoyance.{CapturedInputsAndOutputs, InterestingGivens}
import clairvoyance.state.{TestState, TestStates}
import org.scalatest._

trait ClairvoyantContext extends OneInstancePerTest with InterestingGivens with CapturedInputsAndOutputs { this: Suite =>

  implicit def stringToStep(description: String) = new ClairvoyantStep(description)

  abstract override protected def runTest(testName: String, args: Args): Status = {
    val status: Status = super.runTest(testName, args)
    TestStates += (testName -> TestState(interestingGivens.toList, gatherCapturedValues))
    clearCapturedValues()
    status
  }

  class ClairvoyantStep(description: String) {
    def ===>[T](step: T): T = step
  }
}
