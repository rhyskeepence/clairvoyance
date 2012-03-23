package org.specs2.clairvoyance

trait CapturedInputsAndOutputs {
  def capturedInputsAndOutputs = Seq[ProducesCapturedInputsAndOutputs]()

  def gatherCapturedValues = {
    capturedInputsAndOutputs
      .map(_.producedCapturedInputsAndOutputs)
      .flatten
      .sortBy(_._1)
      .map(_._2)
  }

  def clearCapturedValues() {
    capturedInputsAndOutputs.foreach(_.clear())
  }
}
