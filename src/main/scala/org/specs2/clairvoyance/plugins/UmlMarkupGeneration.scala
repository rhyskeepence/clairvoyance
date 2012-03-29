package org.specs2.clairvoyance.plugins


object UmlMarkupGeneration {

  def generateUmlMarkup(collaborators: Seq[CapturedValueCollaborators]) = {
    collaborators
      .map(toPlantUml)
      .mkString("@startuml\n", "\n", "\n@enduml")
  }

  def toPlantUml: (CapturedValueCollaborators) => String = { collaborator =>
    umlArrow(collaborator.from, collaborator.to, collaborator.what)
  }

  private def umlArrow(from: String, to: String, what: String): String = {
    "\"" + from + "\" ->> \"" + to + "\":" + what
  }
}

