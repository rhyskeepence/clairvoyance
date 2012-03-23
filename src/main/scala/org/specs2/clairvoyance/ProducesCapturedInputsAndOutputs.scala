package org.specs2.clairvoyance

import collection.mutable.ListBuffer
import org.specs2.clairvoyance.Imports._

trait ProducesCapturedInputsAndOutputs {

  private lazy val capturedValues = new ListBuffer[KeyValue]

  def captureValue(capturedValue: KeyValue) {
    capturedValues += capturedValue
  }

  def clear() {
    capturedValues.clear()
  }

  def producedCapturedInputsAndOutputs = capturedValues.toList
}
