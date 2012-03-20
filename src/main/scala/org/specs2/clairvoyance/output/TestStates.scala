package org.specs2.clairvoyance.output

import collection.mutable

object TestStates {
  /* This is mega dodgy but works because the specs are executed sequentially and the ExecutedResult notification is
     triggered immediately after the spec fragment is evaluated.

     Figure out a better way of reporting state to the clairvoyance HTML exporter - perhaps by extending the `in` keyword
     The ExecutedFragment trait is sealed so we can't add our test state to it but perhaps we can fork specs2.
  */
  private val testStates = new mutable.Queue[TestState]

  def +=(testState: TestState) {
    testStates += testState
  }

  def dequeue = testStates.dequeue()
}

case class TestState(interestingGivens: Seq[(String, Any)], capturedInputsAndOutputs: Seq[(String, Any)])

