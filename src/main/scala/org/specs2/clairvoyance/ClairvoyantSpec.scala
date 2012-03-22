package org.specs2.clairvoyance

import org.specs2.mutable.{After, Specification}
import state.{TestState, TestStates}

/**
 * The base class - the only thing to note is that we use mutable specifications rather than immutable.
 * This is not ideal but mutable specs may look a bit scary and magic to the intended audience.
 */
abstract class ClairvoyantSpec extends Specification {
  sequential
  args.report(exporter = "org.specs2.clairvoyance.export.ClairvoyanceHtmlExporting")

  trait ClairvoyantContext extends After with InterestingGivens with CapturedInputsAndOutputs {

    def tearDown {}

    def after {
      tearDown

      TestStates += (this -> TestState(interestingGivens.toList, gatherCapturedValues))
      clearCapturedValues()
    }
  }
}
