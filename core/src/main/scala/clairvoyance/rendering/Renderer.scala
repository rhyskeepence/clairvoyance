package clairvoyance.rendering

import clairvoyance.plugins.{GraphVizDiagram, SvgSequenceDiagram}
import scala.xml.{XML, NodeSeq, PrettyPrinter}

trait Renderer[T] {
  def render: (T => Any)
}

class Rendering(specInstance: Option[CustomRendering]) {
  val defaultRenderer = new UnformattedRender
  val nodeSeqRenderer = new NodeSeqRenderer
  val umlRenderer = new SvgSequenceDiagramRenderer
  val graphVizRenderer = new GraphVizRenderer

  val render = specInstance match {
    case Some(r) => r.customRendering orElse defaultRendering
    case None => defaultRendering
  }

  def defaultRendering: PartialFunction[Any, Any] = {
    case xml: NodeSeq               => nodeSeqRenderer.render(xml)
    case graphViz: GraphVizDiagram  => graphVizRenderer.render(graphViz)
    case svg: SvgSequenceDiagram    => umlRenderer.render(svg)
    case any: Any                   => defaultRenderer.render(any)
  }

  def renderToXml(anything: Any) = render(anything) match {
    case xml: NodeSeq   => <div class='nohighlight'>{xml}</div>
    case Html(html)     => <div class='nohighlight'>{html}</div>
    case any            => <span>{any.toString}</span>
  }
}

class UnformattedRender extends Renderer[Any] {
  def render = identity
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
