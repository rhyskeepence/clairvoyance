package org.specs2.clairvoyance.plugins

object UmlMarkupGeneration {

  def generateUmlMarkup(collaborators: Seq[CapturedValueCollaborators]): String =
    collaborators
      .map(toPlantUml)
      .mkString("@startuml\n", "\n", "\n@enduml")

  def toPlantUml: (CapturedValueCollaborators) => String = umlArrow

  private def umlArrow(message: CapturedValueCollaborators): String =
    s"""\"${message.from}\" ->> \"${message.to}\":<text class=sequence_diagram_clickable sequence_diagram_message_id=${message.id}>${message.what}</text>"""
}
