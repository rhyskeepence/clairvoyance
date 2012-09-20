package org.specs2.clairvoyance.plugins

import java.io.ByteArrayOutputStream
import net.sourceforge.plantuml.{FileFormat, FileFormatOption, SourceStringReader}
import io.Source
import xml.Utility

case class SvgSequenceDiagram(collaborators: Seq[CapturedValueCollaborators]) {

  def toMarkup = {
    val umlMarkup = UmlMarkupGeneration.generateUmlMarkup(collaborators)

    val reader = new SourceStringReader(umlMarkup)
    val os = new ByteArrayOutputStream()
    reader.generateImage(os, new FileFormatOption(FileFormat.SVG))
    os.close()
    new String(os.toByteArray)
  }

}
