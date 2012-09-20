package org.specs2.clairvoyance.rendering

import org.specs2.clairvoyance.plugins.SvgSequenceDiagram
import xml.{XML, Elem, NodeSeq, PrettyPrinter}

trait Renderer[T] {
  def render: (T => Any)
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

  def defaultRendering: PartialFunction[Any, Any] = {
    case xml: NodeSeq => nodeSeqRenderer.render(xml)
    case diagram: SvgSequenceDiagram => umlRenderer.render(diagram)
    case any: Any => defaultRenderer.render(any)
  }

  def renderToXml(anything: Any) = {
    renderingFunction(anything) match {
      case xml: NodeSeq => <div class='nohighlight'>{xml}</div>
      case string: String => <span>{string}</span>
    }
  }
}

class ToStringRender extends Renderer[Any] {
  def render = something => something.toString
}

class NodeSeqRenderer extends Renderer[NodeSeq] {
  val formatter = new PrettyPrinter(80, 2)
  def render = nodes => formatter.formatNodes(nodes)
}

class SvgSequenceDiagramRenderer extends Renderer[SvgSequenceDiagram] {
  def render = diagram => XML.loadString(diagram.toMarkup)
}
