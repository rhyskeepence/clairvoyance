package clairvoyance.rendering

import org.pegdown.{Extensions, LinkRenderer, PegDownProcessor, ToHtmlSerializer}
import scala.io.Source
import scala.xml.NodeSeq
import scala.xml.parsing.XhtmlParser

object Markdown {

  def markdownToXhtml(markdownText: String): NodeSeq = {
    val pegDown = new PegDownProcessor(Extensions.ALL & ~Extensions.QUOTES & ~Extensions.SMARTS, 2000L)
    val html = new ToHtmlSerializer(new LinkRenderer).toHtml(pegDown.parseMarkdown(markdownText.replace("\\\\n", "\n").toCharArray))
    XhtmlParser(Source.fromString(s"<text>$html</text>"))
  }
}
