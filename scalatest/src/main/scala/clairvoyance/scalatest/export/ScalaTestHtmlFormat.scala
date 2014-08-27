package clairvoyance.scalatest.export

import clairvoyance.export.{SpecificationFormatter, FromSource, HtmlFormat}
import clairvoyance.rendering.{CustomRendering, Rendering}
import clairvoyance.rendering.Markdown.markdownToXhtml
import clairvoyance.scalatest.ClairvoyantContext.tagNames
import clairvoyance.rendering.Reflection.tryToCreateObject
import clairvoyance.scalatest.{SkipInteractions, SkipSpecification, Tags}
import clairvoyance.scalatest.tags.{skipInteractions, skipSpecification}
import clairvoyance.state.{TestState, TestStates}
import java.util.UUID
import org.scalatest.events._
import org.scalatest.{Tag, exceptions}
import scala.xml.NodeSeq

case class ScalaTestHtmlFormat (override val xml: NodeSeq = NodeSeq.Empty) extends HtmlFormat(xml) {
  type Self = ScalaTestHtmlFormat

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
    val (suiteClassName, testName, testText, duration) = (event.suiteClassName.get, event.testName, event.testText, event.duration)
    val annotations = annotationsFor(event.suiteName, event.testName)

    val testState = TestStates.dequeue(testName)
    val rendering = renderingFor(suiteClassName)

    <a id={linkNameOf(testText)}></a>
    <div class="testmethod">
      {markdownToXhtml(s"## $testText")}
      <div class="scenario" id={testName.hashCode().toString}>
        { if (!annotations.contains(SkipSpecification))
        <h2>Specification</h2>
        <pre class="highlight specification">{SpecificationFormatter.format(getCodeFrom(suiteClassName, event), suiteClassName = suiteClassName)}</pre>
        }
        <h2>Execution</h2>
        <pre class="highlight results test-passed highlighted">{duration.fold("")(milliseconds => s"Passed in $milliseconds ms")}</pre>
        {interestingGivensTable(testState, rendering)}
        {capturedInputsAndOutputs(testState, rendering, annotations)}
      </div>
    </div>
  }

  private def getCodeFrom(location: String, event: TestSucceeded): List[(Int, String)] = {
    event.location match {
      case Some(LineInFile(ln, _)) => FromSource.getCodeFrom(location, ln)
      case a@_ => FromSource.getCodeFrom(location, event.testText)
    }
  }

  private def renderFragmentForBody(event: TestFailedOrCancelled): NodeSeq = {
    val annotations = annotationsFor(event.suiteName, event.testName)
    val testState = TestStates.dequeue(event.testName)
    val rendering = renderingFor(event.suiteClassName)

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
        { if (!annotations.contains(SkipSpecification))
        <h2>Specification</h2>
        <pre class="highlight specification">{
          val sourceLines = FromSource.getCodeFrom(event.suiteClassName, event.testText)
          SpecificationFormatter.format(sourceLines, event.throwable.get.getStackTrace.toList, event.suiteClassName)
          }</pre>
        }
        <h2>Execution</h2>
        <div class="highlight results test-failed highlighted" style="margin-bottom: 1em">
          { event.duration.fold(NodeSeq.Empty)(milliseconds => <span>{event.name} after {milliseconds} ms</span><br/>)}
          <pre>&gt; { event.message }</pre>
          <span class="detailstoggle">
            <a id={ linkId } href="#" onclick={ s"toggleDetails('$contentId', '$linkId'); return false"}>[ show stacktrace ]</a>
          </span>
          <div id={ contentId } style="display: none; margin-top: 1em">
            { grayStackTraceElements.map((ste: StackTraceElement)  => <div style="color: #CCADAD">{ ste.toString }</div>) }
            { <div style="color: #9C3636; font-weight: bold">{ blackStackTraceElements.head.toString }</div> }
            { blackStackTraceElements.tail.map((ste: StackTraceElement) => <div style="color: #9C3636">{ ste.toString }</div>) }
          </div>
        </div>
        {interestingGivensTable(testState, rendering)}
        {capturedInputsAndOutputs(testState, rendering, annotations)}
      </div>
    </div>
  }

  private def capturedInputsAndOutputs(testState: Option[TestState], rendering: Rendering, annotations: Set[Tag]): NodeSeq = {
    <div style={ if (annotations.contains(SkipInteractions)) "display: none" else "display: inline" }>
    {loggedInputsAndOutputs(testState.map(x => x.copy(x.interestingGivens, x.capturedInputsAndOutputs.filterNot(_.key.matches(".*(Graph|Diagram).*")))), rendering)}
    </div>
    <div stye="display: inline">
    {loggedInputsAndOutputs(testState.map(x => x.copy(x.interestingGivens, x.capturedInputsAndOutputs.filter(_.key.matches(".*(Graph|Diagram).*")))), rendering)}
    </div>
  }

  private def renderFragmentForBody(event: TestPendingOrIgnored): NodeSeq = {
    <a id={linkNameOf(event.testText)}></a>
    <div class="testmethod">
      {markdownToXhtml("## " + event.testText)}<div class="scenario" id={event.testName.hashCode().toString}>
        <h2>Specification</h2>
        <pre class="highlight specification">{SpecificationFormatter.format(FromSource.getCodeFrom(event.suiteClassName, event.testText), suiteClassName = event.suiteClassName)}</pre>
        <h2>Execution</h2>
        <pre class="highlight specification test-not-run">{event.name}</pre>
      </div>
    </div>
  }

  private def renderFragmentForBody(event: MarkupProvided, cssClass: String): NodeSeq = markdownToXhtml(event.text)

  private def stringToPrintWhenNoError(formatter: Option[Formatter], suiteName: String): Option[String] = {
    formatter match {
      case Some(IndentedText(_, rawText, _)) => Some(rawText)
      case Some(MotionToSuppress) => None
      case _ => Some(suiteName)
    }
  }

  private def renderingFor(className: String): Rendering = new Rendering(tryToCreateObject[CustomRendering](className))

  private def annotationsFor(suiteName: String, testName: String): Set[Tag] = {
    val tags = tagNames((suiteName, testName))
    (tags.contains(classOf[skipSpecification].getName), tags.contains(classOf[skipInteractions].getName))
    Tags.declared.filter(t => tags.contains(t.name))
  }
}
