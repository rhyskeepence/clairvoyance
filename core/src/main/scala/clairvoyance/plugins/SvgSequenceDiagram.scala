package clairvoyance.plugins

import java.io.ByteArrayOutputStream
import net.sourceforge.plantuml.{FileFormat, FileFormatOption, SourceStringReader}

case class SvgSequenceDiagram(collaborators: Seq[CapturedValueCollaborators]) {
  def toMarkup = {
    System.setProperty("java.awt.headless", "true")
    val umlMarkup = UmlMarkupGeneration.generateUmlMarkup(collaborators)
    val reader = new SourceStringReader(umlMarkup)
    val os = new ByteArrayOutputStream()
    reader.generateImage(os, new FileFormatOption(FileFormat.SVG))
    os.close()
    new String(os.toByteArray)
  }
}
