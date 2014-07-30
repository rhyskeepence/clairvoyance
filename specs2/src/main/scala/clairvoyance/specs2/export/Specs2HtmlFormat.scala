package clairvoyance.specs2.export

import clairvoyance.export.{FromSource, HtmlFormat, SpecificationFormatter}
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
    def fragmentsOf(s: SpecificationStructure): Seq[Fragment] =
      s.formatFragments(s.map(Fragments.withCreationPaths(Fragments.withSpecName(s.is, s)))).fragments

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
          val link = "#" + linkNameOf(executedResult.s.toString())

          val htmlListItemForResult =
            <li class={cssClassOf(executedResult)}>
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
          val resultOutput = if (executedResult.isSuccess) "Test Passed" else executedResult.result.message
          val testState    = TestStates.dequeue(specificationFullName)

          val optionalCustomRenderer = Reflection.tryToCreateObject[CustomRendering](specificationFullName)
          val rendering = new Rendering(optionalCustomRenderer)

          val sourceLines = FromSource.getCodeFrom(executedResult.location.toString(), executedResult.location.lineNumber)
          val stackTrace: Seq[StackTraceElement] = executedResult.result match {
            case Failure(_, _, st, _) => st
            case _ => Seq.empty
          }

          <a id={linkNameOf(executedResult.s.toString())}></a>
          <div class="testmethod">
            {markdownToXhtml("## " + executedResult.s.raw)}
            <div class="scenario" id={executedResult.hashCode().toString}>
              <h2>Specification</h2>
              <pre class="highlight specification">{SpecificationFormatter.format(sourceLines, stackTrace, specificationFullName)}</pre>
              <h2>Test results:</h2>
              <pre class={s"highlight results ${cssClassOf(executedResult)} highlighted"}>{resultOutput + " in " + executedResult.stats.time}</pre>
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

  private def cssClassOf(executedResult: ExecutedResult): String =
    if (executedResult.isSuccess) "test-passed" else if (executedResult.isSuspended) "test-not-run" else "test-failed"
}
