package org.specs2.clairvoyance.plugins

case class SvgSequenceDiagram(umlMarkup: String) {
  def toMarkup = {
    val reader = new SourceStringReader(umlMarkup);
    val os = new ByteArrayOutputStream();
    reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
    os.close();
    new String(os.toByteArray)
  }
}
