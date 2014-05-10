package clairvoyance.export

import clairvoyance.CapturedValue
import clairvoyance.rendering.Rendering
import clairvoyance.state.TestState
import scala.xml.{NodeBuffer, NodeSeq}

abstract class HtmlFormat(val xml: NodeSeq) {
  type Self <: HtmlFormat

  def printHtml(n: => NodeSeq): Self = print(<html>{n}</html>)

  def printHead(specificationTitle: String): Self = print(xml ++ head(specificationTitle))

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

  protected def interestingGivensTable(testState: Option[TestState], rendering: Rendering): Seq[NodeSeq] = {
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

  protected def loggedInputsAndOutputs(testState: Option[TestState], rendering: Rendering): Seq[NodeBuffer] = {
    val inputsAndOutputs = testState.map(_.capturedInputsAndOutputs).getOrElse(Seq())
    inputsAndOutputs.map {
      case CapturedValue(id, key, value) =>
        <h3 class="logKey" logkey={id.toString}>{key}</h3>
        <div class={"logValue highlight " + value.getClass.getSimpleName }>{rendering.renderToXml(value)}</div>
    }
  }

  protected def print(xml2: NodeSeq): Self

  protected def linkNameOf(formattedResultText: String): String = formattedResultText.replaceAll("\\s", "")

  protected def wordify(title: String): String =
    "(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])".r.replaceAllIn(title, " ")

  protected def formatShortExampleName: String => String = _.split('\n').head
}
