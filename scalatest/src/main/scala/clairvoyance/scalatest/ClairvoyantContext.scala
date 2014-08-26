package clairvoyance.scalatest

import clairvoyance.scalatest.ClairvoyantContext._
import clairvoyance.state.{TestState, TestStates}
import clairvoyance.{CapturedInputsAndOutputs, InterestingGivens}
import org.scalatest._

import scala.collection.mutable

trait ClairvoyantContext extends OneInstancePerTest with InterestingGivens with CapturedInputsAndOutputs { this: Suite =>

  implicit def stringToStep(description: String) = new ClairvoyantStep(description)

  abstract override protected def runTest(testName: String, args: Args): Status = {
    val status = super.runTest(testName, args)
    tagNames += (((suiteName, testName), tags.withDefaultValue(Set.empty)(testName).map(normaliseTagName)))
    TestStates += (testName -> TestState(interestingGivens.toList, gatherCapturedValues))
    clearCapturedValues()
    status
  }

  private def normaliseTagName: (String) => String =
    tagName => if (tagName.startsWith(INTERFACE_PREFIX)) tagName.substring(INTERFACE_PREFIX.length) else tagName

  class ClairvoyantStep(description: String) {
    def ===>[T](step: T): T = step
  }
}

object ClairvoyantContext {
  val INTERFACE_PREFIX = "interface "
  val tagNames = new mutable.HashMap[(String, String), Set[String]]().withDefaultValue(Set.empty)
}
