package clairvoyance.scalatest.export

import java.util.UUID

import clairvoyance.export._
import clairvoyance.rendering.Markdown.markdownToXhtml
import clairvoyance.rendering.Reflection.tryToCreateObject
import clairvoyance.rendering.{CustomRendering, Rendering}
import clairvoyance.scalatest.ClairvoyantContext.tagNames
import clairvoyance.scalatest.{SkipInteractions, SkipSpecification, Tags}
import clairvoyance.state.{TestState, TestStates}
import org.scalatest.events._
import org.scalatest.{Tag, exceptions}

class ScalaTestHtmlFormat extends HtmlFormat {
  def format(specificationTitle: String, suiteResult: SuiteResult): String = {
    head(specificationTitle) + printBody(suiteResult.suiteName, suiteResult)
  }

  private def tableOfContentsFor(suiteResult: SuiteResult): String = {
    s"""
    <ul class="contents">
    ${suiteResult.eventList
      .map {
        case event: TestSucceeded         => renderFragmentForTableOfContents(event)
        case TestFailedOrCancelled(event) => renderFragmentForTableOfContents(event)
        case TestPendingOrIgnored(event)  => renderFragmentForTableOfContents(event)
        case _                            => ""
      }
      .mkString("\n")}
    </ul>
    """
  }

  private def renderFragmentForTableOfContents(event: TestSucceeded): String =
    renderFragmentForTableOfContents(
      event.testName,
      event.testText,
      event.recordedEvents,
      "test-passed",
      "test_passed"
    )

  private def renderFragmentForTableOfContents(event: TestFailedOrCancelled): String =
    renderFragmentForTableOfContents(
      event.testName,
      event.testText,
      event.recordedEvents,
      "test-failed",
      event.cssClass
    )

  private def renderFragmentForTableOfContents(event: TestPendingOrIgnored): String =
    renderFragmentForTableOfContents(
      event.testName,
      event.testText,
      event.recordedEvents,
      "test-not-run",
      event.cssClass
    )

  private def renderFragmentForTableOfContents(
      testName: String,
      testText: String,
      recordedEvents: IndexedSeq[RecordableEvent],
      listCssClass: String,
      cssClass: String
  ): String =
    s"""<li class="$listCssClass">
      <a href="#${linkNameOf(testText)}">
        ${formatShortExampleName(testName)}
      </a>
    </li>"""

  def printBody(specificationTitle: String, suiteResult: SuiteResult): String =
    s"""
    <body>
      <div id="container">
        <h1> ${wordify(specificationTitle)} </h1>
        ${tableOfContentsFor(suiteResult)}
        ${suiteResult.eventList
      .map {
        case ScopeOpenedOrPending(event)  => renderFragmentForBody(event)
        case event: TestSucceeded         => renderFragmentForBody(event)
        case TestFailedOrCancelled(event) => renderFragmentForBody(event)
        case TestPendingOrIgnored(event)  => renderFragmentForBody(event)
        case event: MarkupProvided        => renderFragmentForBody(event, "markup")
        // AlertProvided, InfoProvided, and NoteProvided must not show up in the HTML report
        case _ => ""
      }
      .mkString("\n")}
      </div>
    </body>
    """

  private def renderFragmentForBody(event: ScopeOpenedOrPending): String =
    stringToPrintWhenNoError(event.formatter, event.nameInfo.suiteName) match {
      case Some(string) => markdownToXhtml("# " + string)
      case None         => ""
    }

  private def renderFragmentForBody(event: TestSucceeded): String = {
    val (suiteClassName, testName, testText, duration) =
      (event.suiteClassName.get, event.testName, event.testText, event.duration)
    val annotations = annotationsFor(event.suiteName, event.testName)

    val testState = TestStates.dequeue(testName)
    val rendering = renderingFor(suiteClassName)

    s"""
    <a id="${linkNameOf(testText)}"></a>
    <div class="testmethod">
      ${markdownToXhtml(s"## $testText")}
      <div class="scenario" id="${testName.hashCode().toString}">
        ${renderSpecification(annotations, suiteClassName, event)}
        <h2>Execution</h2>
        <pre class="highlight results test-passed highlighted">${duration.fold("")(
      milliseconds => s"Passed in $milliseconds ms"
    )}</pre>
        ${interestingGivensTable(testState, rendering)}
        ${capturedInputsAndOutputs(testState, rendering, annotations)}
      </div>
    </div>
    """
  }

  private def renderSpecification(
      annotations: Set[Tag],
      suiteClassName: String,
      event: TestSucceeded
  ) = {
    if (!annotations.contains(SkipSpecification)) {
      val spec = SpecificationFormatter.format(
        getCodeFrom(suiteClassName, event),
        Seq.empty,
        suiteClassName,
        codeFormatFor(suiteClassName)
      )

      s"""
        <h2>Specification</h2>
        <pre class="highlight specification">${spec}</pre>
        """
    } else ""
  }

  private def getCodeFrom(location: String, event: TestSucceeded): List[(Int, String)] = {
    event.location match {
      case Some(LineInFile(ln, _, _)) => FromSource.getCodeFrom(location, ln)
      case _                          => FromSource.getCodeFrom(location, event.testText)
    }
  }

  private def renderFragmentForBody(event: TestFailedOrCancelled): String = {
    val annotations = annotationsFor(event.suiteName, event.testName)
    val testState   = TestStates.dequeue(event.testName)
    val rendering   = renderingFor(event.suiteClassName)

    val linkId    = UUID.randomUUID.toString
    val contentId = UUID.randomUUID.toString

    val (grayStackTraceElements, blackStackTraceElements) =
      event.throwable match {
        case Some(throwable) =>
          val stackTraceElements = throwable.getStackTrace.toList
          throwable match {
            case sde: exceptions.StackDepthException =>
              (
                stackTraceElements.take(sde.failedCodeStackDepth),
                stackTraceElements.drop(sde.failedCodeStackDepth)
              )
            case _ => (List(), stackTraceElements)
          }
        case None => (List(), List())
      }

    val specification = {
      if (!annotations.contains(SkipSpecification)) {
        val spec = SpecificationFormatter.format(
          FromSource.getCodeFrom(event.suiteClassName, event.testText),
          event.throwable.get.getStackTrace.toList,
          event.suiteClassName,
          codeFormatFor(event.suiteClassName)
        )

        s"""
        <h2>Specification</h2>
        <pre class="highlight specification">${spec}</pre>
        """
      } else
        ""
    }

    s"""
    <a id="${linkNameOf(event.testText)}"></a>
    <div class="testmethod">
      ${markdownToXhtml("## " + event.testText)}
      <div class="scenario" id="${event.testName.hashCode().toString}">
        ${specification}
        <h2>Execution</h2>
        <div class="highlight results test-failed highlighted" style="margin-bottom: 1em">
          ${event.duration
      .map(milliseconds => s"<span>${event.name} after ${milliseconds} ms</span><br/>")
      .getOrElse("")}
          <pre>&gt; ${event.message}</pre>
          <span class="detailstoggle">
            <a id="${linkId}" href="#" onclick="{ toggleDetails('$contentId', '$linkId'); return false; }">[ show stacktrace ]</a>
          </span>
          <div id="${contentId}" style="display: none; margin-top: 1em">
            ${grayStackTraceElements.map(
      ste => s"""<div style="color: #CCADAD">${ste.toString}</div>)"""
    )}
            <div style="color: #9C3636; font-weight: bold">${blackStackTraceElements.head.toString}</div>
            ${blackStackTraceElements.tail.map(
      ste => s"""<div style="color: #9C3636">${ste.toString}</div>"""
    )}
          </div>
        </div>
        ${interestingGivensTable(testState, rendering).mkString("\n")}
        ${capturedInputsAndOutputs(testState, rendering, annotations)}
      </div>
    </div>
    """
  }

  private def capturedInputsAndOutputs(
      testState: Option[TestState],
      rendering: Rendering,
      annotations: Set[Tag]
  ): String = {
    val interactionStyle =
      if (annotations.contains(SkipInteractions)) "display: none" else "display: inline"

    val interactions = loggedInputsAndOutputs(
      testState.map(
        x =>
          x.copy(
            x.interestingGivens,
            x.capturedInputsAndOutputs.filterNot(_.key.matches(".*(Graph|Diagram).*"))
          )
      ),
      rendering
    )

    val diagrams = loggedInputsAndOutputs(
      testState.map(
        x =>
          x.copy(
            x.interestingGivens,
            x.capturedInputsAndOutputs.filter(_.key.matches(".*(Graph|Diagram).*"))
          )
      ),
      rendering
    )

    s"""
        <div style="${interactionStyle}">${interactions.mkString("\n")}</div>
        <div style="display: inline">${diagrams.mkString("\n")}</div>
      """
  }

  private def renderFragmentForBody(event: TestPendingOrIgnored): String =
    s"""
    <a id={linkNameOf(event.testText)}></a>
    <div class="testmethod">
      ${markdownToXhtml("## " + event.testText)}
      <div class="scenario" id=${event.testName.hashCode().toString}>
        <h2>Specification</h2>
        <pre class="highlight specification">${SpecificationFormatter.format(
      FromSource.getCodeFrom(event.suiteClassName, event.testText),
      Seq.empty,
      event.suiteClassName,
      codeFormatFor(event.suiteClassName)
    )}</pre>
        <h2>Execution</h2>
        <pre class="highlight specification test-not-run">${event.name}</pre>
      </div>
    </div>
  """

  private def renderFragmentForBody(event: MarkupProvided, cssClass: String): String =
    markdownToXhtml(event.text)

  private def stringToPrintWhenNoError(
      formatter: Option[Formatter],
      suiteName: String
  ): Option[String] = {
    formatter match {
      case Some(IndentedText(_, rawText, _)) => Some(rawText)
      case Some(MotionToSuppress)            => None
      case _                                 => Some(suiteName)
    }
  }

  private def renderingFor(className: String): Rendering =
    new Rendering(tryToCreateObject[CustomRendering](className))

  private def codeFormatFor(className: String): CodeFormat =
    tryToCreateObject[CodeFormat](className).getOrElse(DefaultCodeFormat)

  private def annotationsFor(suiteName: String, testName: String): Set[Tag] = {
    val tags = tagNames((suiteName, testName))
    Tags.declared.filter(t => tags.contains(t.name))
  }
}
