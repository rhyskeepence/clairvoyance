package clairvoyance.plugins

import clairvoyance.{CapturedValueSequence, CapturedValue, ProducesCapturedInputsAndOutputs, CapturedInputsAndOutputs}

trait SequenceDiagram extends CapturedInputsAndOutputs with ProducesCapturedInputsAndOutputs {

  def defaultSequenceDiagramActor = "Default"

  abstract override def gatherCapturedValues = {
    val gatheredValues = super.gatherCapturedValues
    val collaborators = CapturedCollaborators.collectCollaborators(gatheredValues, defaultSequenceDiagramActor)
    val sequenceDiagram = CapturedValue(CapturedValueSequence.nextId, "Sequence Diagram", SvgSequenceDiagram(collaborators))

    gatheredValues :+ sequenceDiagram
  }
}
