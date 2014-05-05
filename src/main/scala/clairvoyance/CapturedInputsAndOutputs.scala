package clairvoyance

trait CapturedInputsAndOutputs {
  def capturedInputsAndOutputs = Seq[ProducesCapturedInputsAndOutputs]()

  def gatherCapturedValues: Seq[CapturedValue] =
    capturedInputsAndOutputs
      .map(_.producedCapturedInputsAndOutputs)
      .flatten
      .sortBy(_.id)

  def clearCapturedValues(): Unit = { capturedInputsAndOutputs.foreach(_.clear()) }
}
