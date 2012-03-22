package org.specs2.clairvoyance.rendering

import org.specs2.clairvoyance.plugins.SvgSequenceDiagram
import xml.{XML, Elem, NodeSeq, PrettyPrinter}

trait Renderer[T] {
  def render: (T => Elem)
}

class Rendering(specInstance: Option[CustomRendering]) {
  lazy val defaultRenderer = new ToStringRender
  lazy val nodeSeqRenderer = new NodeSeqRenderer
  lazy val umlRenderer = new SvgSequenceDiagramRenderer

  val renderingFunction =
    specInstance match {
      case Some(r) => r.customRendering orElse defaultRendering
      case None => defaultRendering
    }

  def defaultRendering: PartialFunction[Any, Elem] = {
    case xml: NodeSeq => nodeSeqRenderer.render(xml)
    case diagram: SvgSequenceDiagram => umlRenderer.render(diagram)
    case any: Any => <div>{defaultRenderer.render(any)}</div>
  }

  def renderToXml(anything: Any) = {
    renderingFunction(anything)
  }
}

class ToStringRender extends Renderer[Any] {
  def render = something => <div>{something.toString}</div>
}

class NodeSeqRenderer extends Renderer[NodeSeq] {
  val formatter = new PrettyPrinter(80, 2)
  def render = nodes => <div>formatter.formatNodes(nodes)</div>
}

class SvgSequenceDiagramRenderer extends Renderer[SvgSequenceDiagram] {
  def render = diagram => <div class='nohighlight'>{XML.loadString(diagram.toMarkup)}</div>
}
