package org.specs2.clairvoyance.plugins

import org.specs2.mutable.After
import org.specs2.clairvoyance.{CapturedValueSequence, CapturedValue, ProducesCapturedInputsAndOutputs, CapturedInputsAndOutputs}

trait Graph extends After with CapturedInputsAndOutputs with ProducesCapturedInputsAndOutputs {

  def defaultSequenceDiagramActor = "Default"

  abstract override def gatherCapturedValues = {
    val gatheredValues = super.gatherCapturedValues
    val collaborators = CapturedCollaborators.collectCollaborators(gatheredValues, defaultSequenceDiagramActor)
    val sequenceDiagram = CapturedValue(CapturedValueSequence.nextId, "Graph", GraphVizDiagram(collaborators))

    gatheredValues :+ sequenceDiagram
  }
}