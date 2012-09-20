package org.specs2.clairvoyance.plugins


object UmlMarkupGeneration {

  def generateUmlMarkup(collaborators: Seq[CapturedValueCollaborators]) = {
    collaborators
      .map(toPlantUml)
      .mkString("@startuml\n", "\n", "\n@enduml")
  }

  def toPlantUml: (CapturedValueCollaborators) => String = umlArrow

  private def umlArrow(message: CapturedValueCollaborators): String = {
    "\"%s\" ->> \"%s\":<text class=sequence_diagram_clickable sequence_diagram_message_id=%s>%s</text>".format(message.from, message.to, message.id, message.what)
  }
}

