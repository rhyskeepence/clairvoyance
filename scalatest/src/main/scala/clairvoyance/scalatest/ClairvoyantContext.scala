package clairvoyance.scalatest

import clairvoyance.scalatest.ClairvoyantContext._
import clairvoyance.scalatest.export.{ScalaTestHtmlReporter, ForwardingReporter}
import clairvoyance.state.{TestState, TestStates}
import clairvoyance.{CapturedInputsAndOutputs, InterestingGivens}
import org.scalatest._

import scala.collection.mutable

trait ClairvoyantContext extends SuiteMixin with InterestingGivens with CapturedInputsAndOutputs { this: Suite =>

  implicit def stringToStep(description: String) = new ClairvoyantStep(description)

  abstract override def run(testName: Option[String], args: Args): Status = {
    if(testName.isEmpty) { // so we don't produce a report for every test when OneInstancePerTest
    val reporter: ScalaTestHtmlReporter = new ScalaTestHtmlReporter
      val status: Status = super.run(testName, args.copy(reporter = new ForwardingReporter(args.reporter, reporter)))
      reporter.done()
      status
    } else super.run(testName, args)
  }

  abstract override protected def runTest(testName: String, args: Args): Status = {
    tagNames += (((suiteName, testName), tags.withDefaultValue(Set.empty)(testName).map(normaliseTagName)))
    beforeExecution(testName)

    val status = super.runTest(testName, args)

    afterExecution(testName)
    TestStates += (testName -> TestState(interestingGivens.toList, gatherCapturedValues))
    clearCapturedValues()
    status
  }

  protected def beforeExecution(testName: String): Unit = ()
  protected def afterExecution (testName: String): Unit = ()

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
