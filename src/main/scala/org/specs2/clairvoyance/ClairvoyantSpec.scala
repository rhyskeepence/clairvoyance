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
}
