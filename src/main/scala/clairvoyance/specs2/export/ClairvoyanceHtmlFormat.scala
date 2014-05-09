package clairvoyance.specs2.export

import clairvoyance.CapturedValue
import clairvoyance.export.FromSource
import clairvoyance.rendering.{CustomRendering, Rendering}
import clairvoyance.state.{TestState, TestStates}
import org.specs2.clairvoyance.Specs2Spy.{fragmentsOf, Classes, Markdown}
import org.specs2.specification.{Example, ExecutedFragment, ExecutedResult, ExecutedSpecification, ExecutedText, SpecificationStructure, Text}
import scala.io.Source
import scala.xml.{NodeBuffer, NodeSeq}
import scala.xml.parsing.XhtmlParser
import org.specs2.main.Arguments

case class ClairvoyanceHtmlFormat(xml: NodeSeq = NodeSeq.Empty) {

  lazy val blank = new ClairvoyanceHtmlFormat

  def printHtml(n: => NodeSeq) = print(<html>{n}</html>)

  def printBody(specificationTitle: String, spec: ExecutedSpecification, specAsXml: => NodeSeq) = print(
    <body>
      <div id="container">
        <h1>{wordify(specificationTitle)}</h1>
        {tableOfContentsFor(spec)}
        {specAsXml}
      </div>
    </body>)

  def printSidebar(structure: Seq[SpecificationStructure]): ClairvoyanceHtmlFormat = print(sidebar(structure))

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

  private def formatShortExampleName: String => String = _.split('\n').head

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

  def printHead(specificationTitle: String): ClairvoyanceHtmlFormat = print(xml ++ head(specificationTitle))

  def printFragment(spec: ExecutedSpecification, fragment: ExecutedFragment)(implicit args: Arguments): ClairvoyanceHtmlFormat = {
    print(<ul>
      {fragment match {
        case result: ExecutedResult =>
          val resultCss =
            if (result.isSuccess) "highlight results test-passed highlighted"
            else "highlight results test-failed highlighted"

          val resultOutput =
            if (result.stats.isSuccess) "Test Passed"
            else result.result.message

          val testState = TestStates.dequeue(spec.name.fullName)

          val optionalCustomRenderer = Classes.tryToCreateObject[CustomRendering](spec.name.fullName, printMessage = false, printStackTrace = false)
          val rendering = new Rendering(optionalCustomRenderer)

          <a id={linkNameOf(result.s.toString())}></a>
          <div class="testmethod">
            {markdownToXhtml("## " +result.s.raw)}
            <div class="scenario" id={result.hashCode().toString}>
              <h2>Specification</h2>
              <pre class="highlight specification">{SpecificationFormatter.format(result.result, FromSource.getCodeFrom(result.location.toString(), result.location.lineNumber))}</pre>
              <h2>Test results:</h2>
              <pre class={resultCss}>{resultOutput + " in " + result.stats.time}</pre>
              {interestingGivensTable(testState, rendering)}
              {loggedInputsAndOutputs(testState, rendering)}
            </div>
          </div>

        case text: ExecutedText => markdownToXhtml("# " + text.text)
        case _ => <span></span>
      }}
    </ul>
    )
  }

  private def markdownToXhtml(markdownText: String)(implicit args: Arguments): NodeSeq =
    XhtmlParser(Source.fromString("<text>" + Markdown.toHtmlNoPar(markdownText) + "</text>"))

  private def interestingGivensTable(testState: Option[TestState], rendering: Rendering): Seq[NodeSeq] = {
    val givens = testState.map(_.interestingGivens).getOrElse(Seq())

    givens match {
      case Nil =>
        NodeSeq.Empty
      case _ =>
        <h3 class="logKey">Interesting Givens</h3>
          <table class="interestingGivens logValue">
            {mapInterestingGivenRows(givens, rendering)}
          </table>
    }
  }

  private def mapInterestingGivenRows(givens: Seq[(String, Any)], rendering: Rendering): Seq[NodeSeq] = givens.map {
    case (key: String, value: Any) =>
      <tr>
        <th class="key">{key}</th>
        <td class="interestingGiven">{rendering.renderToXml(value)}</td>
      </tr>
  }

  private def loggedInputsAndOutputs(testState: Option[TestState], rendering: Rendering): Seq[NodeBuffer] = {
    val inputsAndOutputs = testState.map(_.capturedInputsAndOutputs).getOrElse(Seq())
    inputsAndOutputs.map {
      case CapturedValue(id, key, value) =>
        <h3 class="logKey" logkey={id.toString}>{key}</h3>
        <div class={"logValue highlight " + value.getClass.getSimpleName }>{rendering.renderToXml(value)}</div>
    }
  }

  private def head(specificationTitle: String): NodeSeq = {
    <head>
      <title>{specificationTitle}</title>
      <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js" type="text/javascript"></script>
      <script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.7/jquery-ui.min.js" type="text/javascript"></script>

      <link rel="stylesheet" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.7/themes/base/jquery-ui.css" type="text/css" media="all"/>
      <link rel="stylesheet" href="http://static.jquery.com/ui/css/demo-docs-theme/ui.theme.css" type="text/css" media="all"/>
      <link rel="stylesheet" href="css/yatspec.css" type="text/css" media="all"/>

      <script src="javascript/xregexp.js" type="text/javascript"></script>
      <script src="javascript/yatspec.js" type="text/javascript"></script>
      <script src="javascript/sequence_diagram.js" type="text/javascript"></script>
    </head>
  }

  private def print(xml2: NodeSeq): ClairvoyanceHtmlFormat = ClairvoyanceHtmlFormat(xml ++ xml2)

  private def wordify(title: String): String =
    "(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])".r.replaceAllIn(title, " ")

  private def linkNameOf(formattedResultText: String): String = formattedResultText.replaceAll("\\s", "")
}
