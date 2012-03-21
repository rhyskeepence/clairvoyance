package org.specs2.clairvoyance.rendering

import xml.{NodeSeq, PrettyPrinter}

trait Renderer[T] {
  def render: (T => String)
}

class Rendering(specInstance: Option[CustomRendering]) {
  lazy val defaultRenderer = new ToStringRender
  lazy val nodeSeqRenderer = new NodeSeqRenderer

  val renderingFunction =
    specInstance match {
      case Some(r) => r.customRendering orElse defaultRendering
      case None => defaultRendering
    }

  def defaultRendering: PartialFunction[Any, String] = {
    case xml: NodeSeq => nodeSeqRenderer.render(xml)
    case any: Any => defaultRenderer.render(any)
  }

  def renderToString(anything: Any) = {
    renderingFunction(anything)
  }
}

class ToStringRender extends Renderer[Any] {
  def render = something => something.toString
}

class NodeSeqRenderer extends Renderer[NodeSeq] {
  val formatter = new PrettyPrinter(80, 2)
  def render = nodes => formatter.formatNodes(nodes)
}