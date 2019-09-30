package clairvoyance.rendering

import org.pegdown.ast.CodeNode
import org.pegdown.{Extensions, LinkRenderer, PegDownProcessor, ToHtmlSerializer}

import scala.util.matching.Regex

object Markdown {

  def markdownToXhtml(markdown: String): String = {
    val pegDown =
      new PegDownProcessor(Extensions.ALL & ~Extensions.QUOTES & ~Extensions.SMARTS, 2000L)
    val htmlSerializer = new ToHtmlSerializer(new LinkRenderer) {
      override def visit(node: CodeNode): Unit = {
        val text = node.getText
        if (text.contains("\n"))
          printer
            .print("<pre>")
            .print("""<code class="prettyprint">""")
            .printEncoded(text.removeFirst("\n"))
            .print("</code>")
            .print("</pre>")
        else
          printer
            .print("""<code class="prettyprint">""")
            .printEncoded(text)
            .print("</code>")
      }
    }
    val html =
      htmlSerializer.toHtml(pegDown.parseMarkdown(markdown.replace("\\\\n", "\n").toCharArray))
    val htmlWithoutParagraph =
      if (!markdown.contains("\n") || markdown.trim.isEmpty) html.removeEnclosingXmlTag("p")
      else html
    s"<text>$htmlWithoutParagraph</text>"
  }

  implicit def trimmed(s: String): Trimmed = new Trimmed(s)

  class Trimmed(s: String) {
    def removeEnclosingXmlTag(t: String) =
      if (isEnclosing("<" + t, "</" + t + ">"))
        removeFirst("<" + t + ".*?>").trimEnd("</" + t + ">")
      else s

    def isEnclosing(start: String, end: String) = s.startsWith(start) && s.endsWith(end)
    def removeFirst(regex: String)              = new Regex(regex).replaceFirstIn(s, "")
    def trimEnd(end: String)                    = if (s.trim.endsWith(end)) s.trim.dropRight(end.size) else s.trim
  }
}
