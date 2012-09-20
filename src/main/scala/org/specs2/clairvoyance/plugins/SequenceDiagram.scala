package org.specs2.clairvoyance.plugins

import org.specs2.mutable.After
import org.specs2.clairvoyance.{ProducesCapturedInputsAndOutputs, CapturedInputsAndOutputs}

trait SequenceDiagram extends After with CapturedInputsAndOutputs with ProducesCapturedInputsAndOutputs {

  def defaultSequenceDiagramActor = "Default"

  abstract override def gatherCapturedValues = {
    val gatheredValues = super.gatherCapturedValues

    val collaborators = CapturedValues.collectCollaborators(gatheredValues, defaultSequenceDiagramActor)

    val sequenceDiagram = "Sequence Diagram" -> SvgSequenceDiagram(collaborators)

    gatheredValues :+ sequenceDiagram
  }
}
