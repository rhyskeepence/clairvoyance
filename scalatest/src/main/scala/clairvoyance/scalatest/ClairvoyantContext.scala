package clairvoyance.scalatest

import clairvoyance.{CapturedInputsAndOutputs, InterestingGivens}
import clairvoyance.state.{TestState, TestStates}
import org.scalatest._

trait ClairvoyantContext extends BeforeAndAfterEachTestData
  with OneInstancePerTest
  with InterestingGivens
  with CapturedInputsAndOutputs { this: Suite =>

  protected def beforeExecution(): Unit = ()
  protected def afterExecution():  Unit = ()

  override protected final def beforeEach(testData: TestData): Unit = beforeExecution()
  override protected final def afterEach(testData: TestData):  Unit = {
    TestStates += (testData.name -> TestState(interestingGivens.toList, gatherCapturedValues))
    clearCapturedValues()

    afterExecution()
  }

  implicit def stringToStep(description: String) = new ClairvoyantStep(description)

  class ClairvoyantStep(description: String) {
    def ===>[T](step: T): T = step
  }
}
