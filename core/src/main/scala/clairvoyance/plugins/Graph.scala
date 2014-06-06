package clairvoyance.plugins

import clairvoyance.{CapturedValueSequence, CapturedValue, ProducesCapturedInputsAndOutputs, CapturedInputsAndOutputs}

trait Graph extends CapturedInputsAndOutputs with ProducesCapturedInputsAndOutputs {

  def defaultActor = "Default"

  abstract override def gatherCapturedValues = {
    val gatheredValues = super.gatherCapturedValues
    val collaborators = CapturedCollaborators.collectCollaborators(gatheredValues, defaultActor)
    val sequenceDiagram = CapturedValue(CapturedValueSequence.nextId, "Graph", GraphVizDiagram(collaborators))

    gatheredValues :+ sequenceDiagram
  }
}
