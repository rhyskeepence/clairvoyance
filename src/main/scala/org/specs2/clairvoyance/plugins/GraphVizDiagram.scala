package org.specs2.clairvoyance.plugins

case class GraphVizDiagram(collaborators: Seq[CapturedValueCollaborators]) {
  def toMarkup = {
    collaborators
      .map(toArc)
      .mkString("digraph g {", "\n", "}")
  }

  private def toArc(message: CapturedValueCollaborators): String =
    s"""\"${message.from}\" -> \"${message.to}\" [label=\"${message.what}\"]"""
}
