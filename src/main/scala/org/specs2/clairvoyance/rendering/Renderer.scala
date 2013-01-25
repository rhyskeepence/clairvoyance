package org.specs2.clairvoyance.rendering

import org.specs2.clairvoyance.plugins.{GraphVizDiagram, SvgSequenceDiagram}
import xml.{XML, Elem, NodeSeq, PrettyPrinter}

trait Renderer[T] {
  def render: (T => Any)
}

class Rendering(specInstance: Option[CustomRendering]) {
  lazy val defaultRenderer = new ToStringRender
  lazy val nodeSeqRenderer = new NodeSeqRenderer
  lazy val umlRenderer = new SvgSequenceDiagramRenderer
  lazy val graphVizRenderer = new GraphVizRenderer

  val renderingFunction =
    specInstance match {
      case Some(r) => r.customRendering orElse defaultRendering
      case None => defaultRendering
    }

  def defaultRendering: PartialFunction[Any, Any] = {
    case xml: NodeSeq => nodeSeqRenderer.render(xml)
    case graphViz: GraphVizDiagram => graphVizRenderer.render(graphViz)
    case svg: SvgSequenceDiagram => umlRenderer.render(svg)
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

class GraphVizRenderer extends Renderer[GraphVizDiagram] {
  def render = diagram => {
    val url = "https://chart.googleapis.com/chart?cht=gv&chl=" + diagram.toMarkup
    <img src={url}></img>
  }

}
