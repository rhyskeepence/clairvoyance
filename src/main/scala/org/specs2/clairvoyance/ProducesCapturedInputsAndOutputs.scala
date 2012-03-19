package org.specs2.clairvoyance

import collection.mutable.ListBuffer

trait ProducesCapturedInputsAndOutputs {

  private lazy val capturedValues = new ListBuffer[(String, Any)]

  def captureValue[T](capturedValue: (String, T)) {
    capturedValues += capturedValue
  }

  def clear() {
    capturedValues.clear()
  }

  def producedCapturedInputsAndOutputs = capturedValues.toList
}
