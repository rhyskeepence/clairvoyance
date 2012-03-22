package org.specs2.clairvoyance.plugins

import org.specs2.clairvoyance.Imports._

object UmlMarkupGeneration {

  def generateUmlMarkup(capturedValues: KeyValueSequence, defaultActor: String) = {
    capturedValues
      .map(toPlantUml(defaultActor))
      .mkString("@startuml\n", "\n", "\n@enduml")
  }

  val fullyQualifiedMessageSend = """(?i)(.*) from (.*) to (.*)""".r
  val messageSendWithDefaultReceiver = """(?i)(.*) from (.*)""".r
  val messageSendWithDefaultSender = """(?i)(.*) to (.*)""".r

  def toPlantUml(defaultActor: String): (KeyValue) => String = {
    case (capturedValueKey, value) =>
      capturedValueKey match {
        case fullyQualifiedMessageSend(what, from, to) => from + " ->> " + to + ":" + what
        case messageSendWithDefaultReceiver(what, from) => from + " ->> " + defaultActor + ":" + what
        case messageSendWithDefaultSender(what, to) => defaultActor + " ->> " + to + ":" + what
        case _ => ""
      }
  }
}
