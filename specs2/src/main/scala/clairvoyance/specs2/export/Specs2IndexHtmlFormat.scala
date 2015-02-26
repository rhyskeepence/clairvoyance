package clairvoyance.specs2.export

import org.specs2.main.Arguments
import org.specs2.specification._

import scala.xml.NodeSeq

class Specs2IndexHtmlFormat  {

  def printHtml(structures: Seq[SpecificationStructure])(implicit args: Arguments): NodeSeq =
    <html>
      <head>
        <title>Specifications</title>
        <link rel="stylesheet" href="css/yatspec.css" type="text/css" media="all"/>
      </head>
      <body>
        <div class="specifications">{Specs2SpecificationList.list(structures)}</div>
      </body>
    </html>

}
