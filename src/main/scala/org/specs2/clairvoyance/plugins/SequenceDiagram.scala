package org.specs2.clairvoyance.plugins

import org.specs2.mutable.After
import org.specs2.clairvoyance.{ProducesCapturedInputsAndOutputs, CapturedInputsAndOutputs}
import java.io.ByteArrayOutputStream
import net.sourceforge.plantuml.{FileFormat, FileFormatOption, SourceStringReader}

trait SequenceDiagram extends After with CapturedInputsAndOutputs with ProducesCapturedInputsAndOutputs {

  def defaultSequenceDiagramActor = "Default"

  override def capturedInputsAndOutputs = super.capturedInputsAndOutputs :+ this

  abstract override def after {
    val umlMarkup = UmlMarkupGeneration.generateUmlMarkup(gatherCapturedValues, defaultSequenceDiagramActor)
    captureValue("Sequence Diagram" -> SvgSequenceDiagram(umlMarkup))
    super.after
  }
}

case class SvgSequenceDiagram(umlMarkup: String) {
  def toMarkup = {
    val reader = new SourceStringReader(umlMarkup);
    val os = new ByteArrayOutputStream();
    reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
    os.close();
    new String(os.toByteArray)
  }
}