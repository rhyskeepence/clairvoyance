package org.specs2.clairvoyance

trait CapturedInputsAndOutputs {
  def capturedInputsAndOutputs = Seq[ProducesCapturedInputsAndOutputs]()

  def gatherCapturedValues = {
    capturedInputsAndOutputs
      .map(_.producedCapturedInputsAndOutputs)
      .flatten
      .sortBy(_.id)
  }

  def clearCapturedValues() {
    capturedInputsAndOutputs.foreach(_.clear())
  }
}
