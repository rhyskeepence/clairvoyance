package clairvoyance.specs2.export

import clairvoyance.export.{FromSource, HtmlFormat}
import clairvoyance.rendering.{CustomRendering, Rendering}
import clairvoyance.state.TestStates
import org.specs2.clairvoyance.Specs2Spy.{fragmentsOf, Classes, Markdown}
import org.specs2.main.Arguments
import org.specs2.specification.{Example, ExecutedFragment, ExecutedResult, ExecutedSpecification, ExecutedText, SpecificationStructure, Text}
import scala.io.Source
import scala.xml.NodeSeq
import scala.xml.parsing.XhtmlParser

case class Specs2HtmlFormat(override val xml: NodeSeq = NodeSeq.Empty) extends HtmlFormat(xml) {
  type Self = Specs2HtmlFormat

  protected def print(xml2: NodeSeq): Self = Specs2HtmlFormat(this.xml ++ xml2)

  def printBody(specificationTitle: String, spec: ExecutedSpecification, specAsXml: => NodeSeq) = print(
    <body>
      <div id="container">
        <h1>{wordify(specificationTitle)}</h1>
        {tableOfContentsFor(spec)}
        {specAsXml}
      </div>
    </body>)

  def printSidebar(structure: Seq[SpecificationStructure]): Self = print(sidebar(structure))

  private def sidebar(structures: Seq[SpecificationStructure]): NodeSeq = {
    val structure = structures.map { s =>

      val specFragments = fragmentsOf(s).flatMap {
        case Text(text, _) => Some(<li><em>{formatShortExampleName(text.raw)}</em></li>)
        case Example(name, _, _, _, _) => Some(<li> - {formatShortExampleName(name.raw)}</li>)
        case _ => None
      }

      <li>
        <a href={s.identification.url}>{s.identification.title}</a><ul>
        {specFragments}
        </ul>
      </li>
    }

    <div id="sidebar"><ul>{structure}</ul></div>
  }

  private def tableOfContentsFor(spec: ExecutedSpecification): NodeSeq = {
    val items = spec.fragments.foldLeft(("", List[NodeSeq]())) { (accumulator,fragment) =>
      fragment match {
        case executedResult: ExecutedResult =>
          val listClass = if (executedResult.stats.isSuccess) "test-passed" else "test-failed"
          val link = "#" + linkNameOf(executedResult.s.toString())

          val htmlListItemForResult =
            <li class={listClass}>
              <a href={link}>{ formatShortExampleName(accumulator._1) + " " + formatShortExampleName(executedResult.s.raw) }</a>
            </li>

          (accumulator._1, htmlListItemForResult :: accumulator._2)

        case executedText: ExecutedText => (executedText.text, accumulator._2)
        case _ => (accumulator._1, accumulator._2)
      }
    }

    <ul class="contents">{items._2.reverse}</ul>
  }

  def printFragment(specificationFullName: String, fragment: ExecutedFragment)(implicit args: Arguments): Specs2HtmlFormat = {
    print(<ul>
      {fragment match {
        case executedResult: ExecutedResult =>
          val resultCss    = if (executedResult.isSuccess) "highlight results test-passed highlighted" else "highlight results test-failed highlighted"
          val resultOutput = if (executedResult.stats.isSuccess) "Test Passed" else executedResult.result.message
          val testState    = TestStates.dequeue(specificationFullName)

          val optionalCustomRenderer = Classes.tryToCreateObject[CustomRendering](specificationFullName, printMessage = false, printStackTrace = false)
          val rendering = new Rendering(optionalCustomRenderer)

          <a id={linkNameOf(executedResult.s.toString())}></a>
          <div class="testmethod">
            {markdownToXhtml("## " +executedResult.s.raw)}
            <div class="scenario" id={executedResult.hashCode().toString}>
              <h2>Specification</h2>
              <pre class="highlight specification">{SpecificationFormatter.format(executedResult.result, FromSource.getCodeFrom(executedResult.location.toString(), executedResult.location.lineNumber))}</pre>
              <h2>Test results:</h2>
              <pre class={resultCss}>{resultOutput + " in " + executedResult.stats.time}</pre>
              {interestingGivensTable(testState, rendering)}
              {loggedInputsAndOutputs(testState, rendering)}
            </div>
          </div>

        case executedText: ExecutedText => markdownToXhtml("# " + executedText.text)
        case _ => <span></span>
      }}
    </ul>
    )
  }

  private def markdownToXhtml(markdownText: String)(implicit args: Arguments): NodeSeq =
    XhtmlParser(Source.fromString("<text>" + Markdown.toHtmlNoPar(markdownText) + "</text>"))
}
