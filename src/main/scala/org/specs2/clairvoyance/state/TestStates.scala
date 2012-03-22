package org.specs2.clairvoyance.state

import collection.mutable
import org.specs2.clairvoyance.Imports._


object TestStates {
  /* This is mega dodgy but works because the specs are executed sequentially for each class.
     Figure out a better way of reporting state to the clairvoyance HTML exporter.
  */
  private val testStates = new mutable.HashMap[String, mutable.Queue[TestState]]()

  def +=(instanceToTestState: (AnyRef, TestState)) {
    val key = keyNameOf(instanceToTestState._1.getClass)
    if (testStates contains key)
      testStates(key) += instanceToTestState._2
    else
      testStates.put(key, new mutable.Queue += instanceToTestState._2)

  }

  def dequeue(key: String) = testStates.getOrElse(key, new mutable.Queue).dequeueFirst(_ => true)

  private def keyNameOf(spec: Class[_]) = spec.getName.split("\\$")(0)
}

case class TestState(interestingGivens: KeyValueSequence, capturedInputsAndOutputs: KeyValueSequence)

