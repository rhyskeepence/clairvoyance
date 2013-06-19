package org.specs2.clairvoyance.export

import org.specs2.specification._
import org.specs2.clairvoyance.state.TestStates
import org.specs2.reflect.Classes
import org.specs2.clairvoyance.rendering.{CustomRendering, Rendering}
import xml._
import org.specs2.specification.ExecutedSpecification
import org.specs2.clairvoyance.state.TestState
import org.specs2.specification.ExecutedText
import org.specs2.clairvoyance.CapturedValue
import org.specs2.main.Arguments

case class ClairvoyanceHtmlFormat(xml: NodeSeq = NodeSeq.Empty) {

  lazy val blank = new ClairvoyanceHtmlFormat

  def printHtml(n: => NodeSeq) = print(<html>{n}</html>)

  def printBody(spec: ExecutedSpecification, n: => NodeSeq) = print(<body>
    <div id="container">
      <h1>
        {wordify(spec.name.title)}
      </h1>{tableOfContentsFor(spec)}{n}
    </div>
  </body>)

  def printSidebar(structure: Seq[SpecificationStructure]) = {
    print(sidebar(structure))
  }

  def sidebar(structures: Seq[SpecificationStructure]) = {
    val structure = structures.map { s =>

      val specFragments = s.content.fragments.flatMap {
        case org.specs2.specification.Text(text, _) => Some(<li><em>{text}</em></li>)
        case Example(name, _, _, _, _) => Some(<li> - {name}</li>)
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

  def tableOfContentsFor(spec: ExecutedSpecification) = {
    val items = spec.fragments.foldLeft(("", List[NodeSeq]())) { (accumulator,fragment) =>
      fragment match {
        case executedResult: ExecutedResult =>
          val listClass = if (executedResult.stats.isSuccess) "test-passed" else "test-failed"
          val link = "#" + linkNameOf(executedResult)

          val htmlListItemForResult =
            <li class={listClass}>
              <a href={link}>{ accumulator._1 + " " + executedResult.s.raw }</a>
            </li>
          
          (accumulator._1, htmlListItemForResult :: accumulator._2)

        case executedText: ExecutedText =>
          (executedText.text, accumulator._2)

        case _ =>
          (accumulator._1, accumulator._2)

      }
    }

    <ul class="contents">{items._2.reverse}</ul>
  }

  def printHead(spec: ExecutedSpecification) = print(xml ++ head(spec))

  def printFragment(spec: ExecutedSpecification, fragment: ExecutedFragment) = {
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

          val optionalCustomRenderer = Classes.tryToCreateObject[CustomRendering](spec.name.fullName, false, false)
          val rendering = new Rendering(optionalCustomRenderer)

          <a id={linkNameOf(result)}></a>
          <div class="testmethod">
            <h2>
              {result.s.toXml}
            </h2>
            <div class="scenario" id={result.hashCode().toString}>
              <h2>Specification</h2>
              <pre class="highlight specification">{SpecificationFormatter.format(result.result, FromSource.getCodeFrom(result.location))}</pre>
              <h2>Test results:</h2>
              <pre class={resultCss}>{resultOutput + " in " + result.stats.time}</pre>
              {interestingGivensTable(testState, rendering)}
              {loggedInputsAndOutputs(testState, rendering)}
            </div>
          </div>

        case text: ExecutedText => {
          <h1>
            {text.text}
          </h1>
        }

        case _ => <span></span>
      }}
    </ul>
    )
  }

  def interestingGivensTable(testState: Option[TestState], rendering: Rendering) = {
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

  def mapInterestingGivenRows(givens: Seq[(String, Any)], rendering: Rendering) = {
    givens.map {
      case (key: String, value: Any) =>
        <tr>
          <th class="key">{key}</th>
          <td class="interestingGiven">{rendering.renderToXml(value)}</td>
        </tr>
    }
  }

  def loggedInputsAndOutputs(testState: Option[TestState], rendering: Rendering) = {
    val inputsAndOutputs = testState.map(_.capturedInputsAndOutputs).getOrElse(Seq())
    inputsAndOutputs.map {
      case CapturedValue(id, key, value) =>
        <h3 class="logKey" logkey={id.toString}>{key}</h3>
        <div class={"logValue highlight " + value.getClass.getSimpleName }>{rendering.renderToXml(value)}</div>
    }
  }

  def head(spec: ExecutedSpecification) = {
    <head>
      <title>
        {spec.name}
      </title>
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

  private def print(xml2: NodeSeq): ClairvoyanceHtmlFormat = {
    ClairvoyanceHtmlFormat(xml ++ xml2)
  }

  private def print(xml2: Elem): ClairvoyanceHtmlFormat = {
    ClairvoyanceHtmlFormat(xml ++ xml2)
  }

  private def wordify(title: String) = {
    "%s|%s|%s".format(
      "(?<=[A-Z])(?=[A-Z][a-z])",
      "(?<=[^A-Z])(?=[A-Z])",
      "(?<=[A-Za-z])(?=[^A-Za-z])"
    ).r.replaceAllIn(title, " ")
  }

  private def linkNameOf(fragment: ExecutedResult) = fragment.s.toString.replaceAll("\\s", "")

}
