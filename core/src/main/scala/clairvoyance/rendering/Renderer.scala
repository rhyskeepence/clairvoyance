package clairvoyance.rendering

import java.net.URLEncoder
import java.nio.charset.Charset

import clairvoyance.plugins.{GraphVizDiagram, SvgSequenceDiagram}

trait Renderer[T] {
  def render: T => Any
}

class Rendering(specInstance: Option[CustomRendering]) {
  val defaultRenderer  = new UnformattedRender
  val umlRenderer      = new SvgSequenceDiagramRenderer
  val graphVizRenderer = new GraphVizRenderer

  val render: PartialFunction[Any, Any] = specInstance match {
    case Some(r) => r.customRendering orElse defaultRendering
    case None    => defaultRendering
  }

  def defaultRendering: PartialFunction[Any, Any] = {
    case graphViz: GraphVizDiagram => graphVizRenderer.render(graphViz)
    case svg: SvgSequenceDiagram   => umlRenderer.render(svg)
    case any: Any                  => defaultRenderer.render(any)
  }

  def renderToXml(anything: Any): String = render(anything) match {
    case Html(html)   => s"<div class='nohighlight'>$html</div>"
    case any          => s"<span>${any.toString}</span>"
  }
}

class UnformattedRender extends Renderer[Any] {
  def render = identity
}

class SvgSequenceDiagramRenderer extends Renderer[SvgSequenceDiagram] {
  def render = diagram => Html(diagram.toMarkup)
}

class GraphVizRenderer extends Renderer[GraphVizDiagram] {
  def render = diagram => {
    val url = "https://chart.googleapis.com/chart?cht=gv&chl=" + URLEncoder.encode(diagram.toMarkup, Charset.defaultCharset)
    Html(s"<img src=$url></img>")
  }
}
