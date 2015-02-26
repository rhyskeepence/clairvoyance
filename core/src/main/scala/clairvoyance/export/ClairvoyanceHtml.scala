package clairvoyance.export

import scala.xml.NodeSeq

case class ClairvoyanceHtml(url: String, xml: NodeSeq, notifyUser: Boolean = true)
