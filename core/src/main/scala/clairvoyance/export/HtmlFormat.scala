package clairvoyance.export

import clairvoyance.CapturedValue
import clairvoyance.rendering.Rendering
import clairvoyance.state.TestState
import scala.util.Properties.lineSeparator

trait HtmlFormat {

  def head(specificationTitle: String): String = {
    s"""<head>
    <title>${specificationTitle}</title>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js" type="text/javascript"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.7/jquery-ui.min.js" type="text/javascript"></script>

    <link rel="stylesheet" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.7/themes/base/jquery-ui.css" type="text/css" media="all"/>
    <link rel="stylesheet" href="http://static.jquery.com/ui/css/demo-docs-theme/ui.theme.css" type="text/css" media="all"/>
    <link rel="stylesheet" href="css/yatspec.css" type="text/css" media="all"/>

    <script src="javascript/xregexp.js" type="text/javascript"></script>
    <script src="javascript/yatspec.js" type="text/javascript"></script>
    <script src="javascript/sequence_diagram.js" type="text/javascript"></script>
    <script type="text/javascript">
       function toggleDetails(contentId, linkId) {
         var content  = document.getElementById(contentId);
         var linkText = document.getElementById(linkId);
         if (content.style.display == \"block\") {
           content.style.display = \"none\";
           linkText.innerHTML = \"[ show stacktrace ]\";
         } else {
           content.style.display = \"block\";
           linkText.innerHTML = \"[ hide stacktrace ]\";
         }
       }

       function hideOpenInNewTabIfRequired() {
         if (top === self) { document.getElementById('printlink').style.display = 'none'; }
       }
  </script>
</head>"""
  }

  protected def interestingGivensTable(
      testState: Option[TestState],
      rendering: Rendering
  ): String = {
    testState.toSeq.flatMap(_.interestingGivens).map {
      case (key: String, value: Any) =>
        s"""
          <h3 class="logKey">Interesting Givens</h3>
          <table class="interestingGivens logValue">
           <tr>
              <th class="key">${key}</th>
              <td class="interestingGiven">${rendering.renderToXml(value)}</td>
              </tr>
          </table>"""
    }.mkString("\n")
  }

  protected def loggedInputsAndOutputs(
      testState: Option[TestState],
      rendering: Rendering
  ): Seq[String] = {
    testState.toSeq.flatMap(_.capturedInputsAndOutputs).map {
      case CapturedValue(id, key, value) =>
        s"""
        <h3 class="logKey" logkey="${id.toString}">${key}</h3>
        <div class="logValue highlight monospace ${value.getClass.getSimpleName}">${rendering.renderToXml(value)}</div>
        """
    }
  }

  protected def linkNameOf(formattedResultText: String): String =
    formattedResultText.replaceAll("\\s", "")

  protected def wordify(title: String): String =
    "(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])".r
      .replaceAllIn(title, " ")

  protected def formatShortExampleName: String => String = _.split(lineSeparator).head
}
