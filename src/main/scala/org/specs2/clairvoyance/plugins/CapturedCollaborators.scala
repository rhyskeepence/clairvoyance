package org.specs2.clairvoyance.plugins

import org.specs2.clairvoyance.CapturedValue

object CapturedCollaborators {
  def collectCollaborators(capturedValues: Seq[CapturedValue], defaultActor: String) = {
    capturedValues.map(toCapturedValueCollaborators(defaultActor)).flatten
  }

  val fullyQualifiedMessageSend = """(?i)(.*) from (.*) to (.*)""".r
  val messageSendWithDefaultReceiver = """(?i)(.*) from (.*)""".r
  val messageSendWithDefaultSender = """(?i)(.*) to (.*)""".r

  def toCapturedValueCollaborators(defaultActor: String): (CapturedValue) => Option[CapturedValueCollaborators] = {
    case CapturedValue(id, capturedValueKey, value) =>
      capturedValueKey match {
        case fullyQualifiedMessageSend(what, from, to) => Some(CapturedValueCollaborators(id, capturedValueKey, from, to, what, value))
        case messageSendWithDefaultReceiver(what, from) => Some(CapturedValueCollaborators(id, capturedValueKey, from, defaultActor, what, value))
        case messageSendWithDefaultSender(what, to) => Some(CapturedValueCollaborators(id, capturedValueKey, defaultActor, to, what, value))
        case _ => None
      }
  }
}

case class CapturedValueCollaborators(id: Int, key: String, from: String, to: String, what: String, rawMessage: Any)
