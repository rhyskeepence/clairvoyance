package org.specs2.clairvoyance.output

import scala.xml._
import org.specs2.specification.{ExecutedText, ExecutedResult, ExecutedFragment, ExecutedSpecification}

case class ClairvoyanceHtmlFormat(xml: NodeSeq = NodeSeq.Empty) {

  lazy val blank = new ClairvoyanceHtmlFormat

  def printHtml(n: => NodeSeq) = print(<html>
    {n}
  </html>)

  def printBody(spec: ExecutedSpecification, n: => NodeSeq) = print(<body>
    <div id="container">
      <h1>{spec.name}</h1>
      {n}
    </div>
  </body>)

  def printHead(spec: ExecutedSpecification) = print(xml ++ head(spec))

  def printFragment(fragment: ExecutedFragment) = {
    print(<ul>
      {fragment match {
        case result: ExecutedResult =>
          val resultCss =
            if (result.isSuccess) "highlight results test-passed highlighted"
            else "highlight results test-failed highlighted"

          val resultOutput =
            if (result.stats.isSuccess) "Test Passed"
            else result.result.message

          val testState = TestStates.dequeue

          <div class="testmethod">
            <h2>
              {result.s.toXml}
            </h2>
            <div class="scenario" id=" ">
              <a id=""></a>
              <h2>Specification</h2>
              <pre class="highlight specification">{SpecificationFormatter.format(FromSource.getCodeFrom(result.location))}</pre>
              <h2>Test results:</h2>
              <pre class={resultCss}>{resultOutput}</pre>

              {interestingGivensTable(testState)}
              {loggedInputsAndOutputs(testState)}
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

  def interestingGivensTable(testState: TestState) = {
    val givens = testState.interestingGivens

    givens match {
      case Nil =>
        NodeSeq.Empty
      case _ =>
        <h3 class="logKey">Interesting Givens</h3>
        <table class="interestingGivens logValue">
          {mapInterestingGivenRows(givens)}
        </table>
    }
  }

  def mapInterestingGivenRows(givens: Seq[(String,Any)]) = {
    givens.map {
      case (key: String, value: Any) =>
        <tr>
          <th class="key">{key}</th>
          <td class="interestingGiven">{Rendering.renderToString(value)}</td>
        </tr>
    }
  }

  def loggedInputsAndOutputs(testState: TestState) = {
    testState.capturedInputsAndOutputs.map {
      case (key: String, value: Any) =>
        <h3 class="logKey" logkey={key.replaceAll("\\s", "_")}>{key}</h3>
        <div class="logValue highlight String">{Rendering.renderToString(value)}</div>
    }
  }

  def head(spec: ExecutedSpecification) =
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
    </head>

  private def print(xml2: NodeSeq): ClairvoyanceHtmlFormat = {
    ClairvoyanceHtmlFormat(xml ++ xml2)
  }

  private def print(xml2: Elem): ClairvoyanceHtmlFormat = {
    ClairvoyanceHtmlFormat(xml ++ xml2)
  }
}
