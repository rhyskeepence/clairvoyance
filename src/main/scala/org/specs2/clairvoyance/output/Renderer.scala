package org.specs2.clairvoyance.output

import xml.{PrettyPrinter, NodeSeq}


object Rendering {
  lazy val defaultRenderer = new ToStringRender
  lazy val nodeSeqRenderer = new NodeSeqRenderer

  def renderToString(anything: Any) = {
    anything match {
      case xml: NodeSeq => nodeSeqRenderer.render(xml)
      case any: Any => defaultRenderer.render(any)
    }
  }
}

trait Renderer[T] {
  def render: (T => String)
}

class ToStringRender extends Renderer[Any] {
  def render = something => something.toString
}

class NodeSeqRenderer extends Renderer[NodeSeq] {
  val formatter = new PrettyPrinter(80, 2)
  def render = nodes => formatter.formatNodes(nodes)
}


