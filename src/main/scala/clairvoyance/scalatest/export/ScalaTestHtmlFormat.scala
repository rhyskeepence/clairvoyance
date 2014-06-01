package clairvoyance.scalatest.export

import clairvoyance.export.{SpecificationFormatter, FromSource, HtmlFormat}
import clairvoyance.rendering.Markdown.markdownToXhtml
import clairvoyance.rendering.{CustomRendering, Reflection, Rendering}
import clairvoyance.state.TestStates
import java.util.UUID
import org.pegdown.PegDownProcessor
import org.scalatest.events._
import org.scalatest.exceptions
import org.scalatest.tools.clairvoyance.ScalaTestSpy.SuiteResult
import scala.util.Properties.lineSeparator
import scala.xml.{NodeSeq, XML}

case class ScalaTestHtmlFormat (override val xml: NodeSeq = NodeSeq.Empty) extends HtmlFormat(xml) {
  type Self = ScalaTestHtmlFormat

  private val specIndent = 15

  protected def print(xml2: NodeSeq): Self = ScalaTestHtmlFormat(xml ++ xml2)

  def printSidebar(results: Seq[SuiteResult]): Self = print(sidebar(results))

  private def sidebar(results: Seq[SuiteResult]): NodeSeq = {
    val summarisedSuites = results.map { suite =>
      <li>
        <a href={ s"${suite.suiteId}.html" }>{suite.suiteName}</a><ul>
        {
          suite.eventList.flatMap {
            case event: ScopeOpened => stringToPrintWhenNoError(event.formatter, event.nameInfo.suiteName) match {
              case Some(text) => Some(<li><em>{formatShortExampleName(text)}</em></li>)
              case None       => NodeSeq.Empty
            }
            case event: TestStarting => Some(<li> - {formatShortExampleName(event.testText)}</li>)
            case _ => None
          }
        }
        </ul>
      </li>
    }
    <div id="sidebar"><ul>{summarisedSuites}</ul></div>
  }

  private def tableOfContentsFor(suiteResult: SuiteResult): NodeSeq = {
    <ul class="contents">
    {
      suiteResult.eventList.map {
        case event: TestSucceeded         => renderFragmentForTableOfContents(event)
        case TestFailedOrCancelled(event) => renderFragmentForTableOfContents(event)
        case TestPendingOrIgnored (event) => renderFragmentForTableOfContents(event)
        case _ => NodeSeq.Empty
      }
    }
    </ul>
  }

  private def renderFragmentForTableOfContents(event: TestSucceeded): NodeSeq =
    renderFragmentForTableOfContents(event.testName, event.testText, event.recordedEvents, "test-passed", "test_passed")

  private def renderFragmentForTableOfContents(event: TestFailedOrCancelled): NodeSeq =
    renderFragmentForTableOfContents(event.testName, event.testText, event.recordedEvents, "test-failed", event.cssClass)

  private def renderFragmentForTableOfContents(event: TestPendingOrIgnored): NodeSeq =
    renderFragmentForTableOfContents(event.testName, event.testText, event.recordedEvents, "test-not-run", event.cssClass)

  private def renderFragmentForTableOfContents(testName: String, testText: String,
                                               recordedEvents: IndexedSeq[RecordableEvent],
                                               listCssClass: String, cssClass: String): NodeSeq =
    <li class={listCssClass}>
      <a href={"#" + linkNameOf(testText)}>
        {formatShortExampleName(testName)}
      </a>
    </li>

  def printBody(specificationTitle: String, suiteResult: SuiteResult): Self = print(
    <body>
      <div id="container">
        <h1> {wordify(specificationTitle)} </h1>
        {tableOfContentsFor(suiteResult)}
        {
          suiteResult.eventList.map {
            case ScopeOpenedOrPending(event)  => renderFragmentForBody(event)
            case event: TestSucceeded         => renderFragmentForBody(event)
            case TestFailedOrCancelled(event) => renderFragmentForBody(event)
            case TestPendingOrIgnored(event)  => renderFragmentForBody(event)
            case event: MarkupProvided        => renderFragmentForBody(event, "markup")
            // AlertProvided, InfoProvided, and NoteProvided must not show up in the HTML report
            case _ => NodeSeq.Empty
          }
        }
      </div>
    </body>
  )

  private def renderFragmentForBody(event: ScopeOpenedOrPending): NodeSeq =
    stringToPrintWhenNoError(event.formatter, event.nameInfo.suiteName) match {
      case Some(string) => markdownToXhtml("# " + string)
      case None => NodeSeq.Empty
    }

  private def renderFragmentForBody(event: TestSucceeded): NodeSeq = {
    val (suiteClassName, testName, testText, duration) =
      (event.suiteClassName, event.testName, event.testText, event.duration)

    val testState = TestStates.dequeue(testName)
    val rendering = new Rendering(Reflection.tryToCreateObject[CustomRendering](testName))

    <a id={linkNameOf(testText)}></a>
    <div class="testmethod">
      {markdownToXhtml(s"## $testText")}
      <div class="scenario" id={testName.hashCode().toString}>
        <h2>Specification</h2>
        <pre class="highlight specification">{SpecificationFormatter.format(FromSource.getCodeFrom(suiteClassName.get, testText))}</pre>
        <h2>Execution</h2>
        <pre class="highlight results test-passed highlighted">{duration.fold("")(milliseconds => s"Passed in $milliseconds ms")}</pre>
        {interestingGivensTable(testState, rendering)}
        {loggedInputsAndOutputs(testState, rendering)}
      </div>
    </div>
  }

  private def renderFragmentForBody(event: TestFailedOrCancelled): NodeSeq = {
    val testState = TestStates.dequeue(event.testName)
    val optionalCustomRenderer = Reflection.tryToCreateObject[CustomRendering](event.testName)
    val rendering = new Rendering(optionalCustomRenderer)

    val linkId    = UUID.randomUUID.toString
    val contentId = UUID.randomUUID.toString

    val (grayStackTraceElements, blackStackTraceElements) =
      event.throwable match {
        case Some(throwable) =>
          val stackTraceElements = throwable.getStackTrace.toList
          throwable match {
            case sde: exceptions.StackDepthException => (stackTraceElements.take(sde.failedCodeStackDepth), stackTraceElements.drop(sde.failedCodeStackDepth))
            case _ => (List(), stackTraceElements)
          }
        case None => (List(), List())
      }

    <a id={linkNameOf(event.testText)}></a>
    <div class="testmethod">
      {markdownToXhtml("## " + event.testText)}
      <div class="scenario" id={event.testName.hashCode().toString}>
        <h2>Specification</h2>
        <pre class="highlight specification">{
          val sourceLines = FromSource.getCodeFrom(event.suiteClassName.get, event.testText)
          SpecificationFormatter.format(sourceLines, event.throwable.get.getStackTrace.toList)
          }</pre>
        <h2>Execution</h2>
        <div class="highlight results test-failed highlighted" style="margin-bottom: 1em">
          { event.duration.fold(NodeSeq.Empty)(milliseconds => <span>{event.name} after {milliseconds} ms</span><br/>)}
          <span>&gt; { event.message }</span>
          <span class="detailstoggle"><a id={ linkId } href="#" onclick={ s"toggleDetails('$contentId', '$linkId'); return false"}>(Show Details)</a></span>
          <div id={ contentId } style="display: none; margin-top: 1em">
            { grayStackTraceElements.map((ste: StackTraceElement)  => <div style="color: #CCADAD">{ ste.toString }</div>) }
            { <div style="color: #9C3636; font-weight: bold">{ blackStackTraceElements.head.toString }</div> }
            { blackStackTraceElements.tail.map((ste: StackTraceElement) => <div style="color: #9C3636">{ ste.toString }</div>) }
          </div>
        </div>
        {interestingGivensTable(testState, rendering)}
        {loggedInputsAndOutputs(testState, rendering)}
      </div>
    </div>
  }

  private def renderFragmentForBody(event: TestPendingOrIgnored): NodeSeq = {
    <a id={linkNameOf(event.testText)}></a>
    <div class="testmethod">
      {markdownToXhtml("## " + event.testText)}<div class="scenario" id={event.testName.hashCode().toString}>
        <h2>Specification</h2>
        <pre class="highlight specification">{SpecificationFormatter.format(FromSource.getCodeFrom(event.suiteClassName.get, event.testText))}</pre>
        <h2>Execution</h2>
        <pre class="highlight specification test-not-run">{event.name}</pre>
      </div>
    </div>
  }

  private def renderFragmentForBody(event: MarkupProvided, cssClass: String): NodeSeq =
    markup(generateElementId, event.text, getIndentLevel(event.formatter) + 1, cssClass)

  private def getIndentLevel(formatter: Option[Formatter]): Int = formatter match {
    case Some(IndentedText(formattedText, rawText, indentationLevel)) => indentationLevel
    case _ => 0
  }

  private def stringToPrintWhenNoError(formatter: Option[Formatter], suiteName: String): Option[String] = {
    formatter match {
      case Some(IndentedText(_, rawText, _)) => Some(rawText)
      case Some(MotionToSuppress) => None
      case _ => Some(suiteName)
    }
  }

  private def generateElementId = UUID.randomUUID.toString

  // TODO show the exception in the HTML report rather than blowing up the reporter
  // because that means the whole suite doesn't get recorded. May want to do this more generally though.
  private def markup(elementId: String, text: String, indentLevel: Int, styleName: String) = {
    val pegDown = new PegDownProcessor
    val htmlString = convertSingleParagraphToDefinition(pegDown.markdownToHtml(text))
    <div id={ elementId } class={ styleName } style={ "margin-left: " + (specIndent * twoLess(indentLevel)) + "px;" }>
      {
        try XML.loadString(htmlString)
        catch {
          case e: Exception => XML.loadString(s"<div>$htmlString</div>")
        }
      }
    </div>
  }

  private def convertSingleParagraphToDefinition(html: String): String = {
    val firstOpenParagraph = html.indexOf("<p>")
    if (firstOpenParagraph == 0 && html.indexOf("<p>", 1) == -1 && html.indexOf("</p>") == html.length - 4)
      html.replace("<p>", s"<dl>$lineSeparator<dt>").replace("</p>", s"</dt>$lineSeparator</dl>")
    else html
  }

  private def twoLess(indentationLevel: Int): Int = indentationLevel - 2 match {
    case level if level < 0 => 0
    case level => level
  }
}
