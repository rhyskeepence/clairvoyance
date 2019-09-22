package clairvoyance.specs2.export

import clairvoyance.export._
import clairvoyance.rendering.Reflection._
import clairvoyance.rendering.{CustomRendering, Reflection, Rendering}
import clairvoyance.rendering.Markdown.markdownToXhtml
import clairvoyance.state.TestStates
import org.specs2.execute.Failure
import org.specs2.main.Arguments
import org.specs2.specification._
import scala.xml.NodeSeq

case class Specs2HtmlFormat(override val xml: NodeSeq = NodeSeq.Empty) extends HtmlFormat(xml) {
  type Self = Specs2HtmlFormat

  protected def print(xml2: NodeSeq): Self = Specs2HtmlFormat(this.xml ++ xml2)

  def printBody(specificationTitle: String, spec: ExecutedSpecification, specAsXml: => NodeSeq) =
    print(<body>
      <div id="container">
        <h1><a href="index.html">Specs</a> / {wordify(specificationTitle)}</h1>
        {tableOfContentsFor(spec)}
        {specAsXml}
      </div>
    </body>)

  private def tableOfContentsFor(spec: ExecutedSpecification): NodeSeq = {
    val items = spec.fragments.foldLeft(("", List[NodeSeq]())) { (accumulator, fragment) =>
      fragment match {
        case executedResult: ExecutedResult =>
          val link = "#" + linkNameOf(executedResult.s.toString())

          val htmlListItemForResult =
            <li class={cssClassOf(executedResult)}>
              <a href={link}>{
              formatShortExampleName(accumulator._1) + " " + formatShortExampleName(
                executedResult.s.raw
              )
            }</a>
            </li>

          (accumulator._1, htmlListItemForResult :: accumulator._2)

        case executedText: ExecutedText => (executedText.text, accumulator._2)
        case _                          => (accumulator._1, accumulator._2)
      }
    }

    <ul class="contents">{items._2.reverse}</ul>
  }

  def printFragment(specificationFullName: String, fragment: ExecutedFragment)(
      implicit args: Arguments
  ): Specs2HtmlFormat = {
    print(<ul>
      {
      fragment match {
        case executedResult: ExecutedResult =>
          val resultOutput =
            if (executedResult.isSuccess) "Test Passed" else executedResult.result.message
          val testState = TestStates.dequeue(specificationFullName)

          val optionalCustomRenderer =
            Reflection.tryToCreateObject[CustomRendering](specificationFullName)
          val rendering = new Rendering(optionalCustomRenderer)

          val sourceLines = FromSource
            .getCodeFrom(executedResult.location.toString(), executedResult.location.lineNumber)
          val stackTrace: Seq[StackTraceElement] = executedResult.result match {
            case Failure(_, _, st, _) => st
            case _                    => Seq.empty
          }

          <a id={linkNameOf(executedResult.s.toString())}></a>
          <div class="testmethod">
            {markdownToXhtml("## " + executedResult.s.raw)}
            <div class="scenario" id={executedResult.hashCode().toString}>
              <h2>Specification</h2>
              <pre class="highlight specification">{
            SpecificationFormatter.format(
              sourceLines,
              stackTrace,
              specificationFullName,
              codeFormatFor(specificationFullName)
            )
          }</pre>
              <h2>Test results:</h2>
              <pre class={s"highlight results ${cssClassOf(executedResult)} highlighted"}>{
            resultOutput + " in " + executedResult.stats.time
          }</pre>
              {interestingGivensTable(testState, rendering)}
              {loggedInputsAndOutputs(testState, rendering)}
            </div>
          </div>

        case executedText: ExecutedText => markdownToXhtml("# " + executedText.text)
        case _                          => <span></span>
      }
    }
    </ul>)
  }

  private def codeFormatFor(className: String): CodeFormat =
    tryToCreateObject[CodeFormat](className).getOrElse(DefaultCodeFormat)

  private def cssClassOf(executedResult: ExecutedResult): String =
    if (executedResult.isSuccess) "test-passed"
    else if (executedResult.isSuspended) "test-not-run"
    else "test-failed"
}
