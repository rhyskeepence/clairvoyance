package org.specs2.clairvoyance

import org.specs2.mutable.{After, Specification}
import state.{TestState, TestStates}

abstract class ClairvoyantSpec extends Specification {
  sequential
  args.report(exporter = "org.specs2.clairvoyance.export.ClairvoyanceHtmlExporting")
}
