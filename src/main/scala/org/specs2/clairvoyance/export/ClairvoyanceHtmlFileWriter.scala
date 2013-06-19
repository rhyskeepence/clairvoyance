package org.specs2.clairvoyance.export

import org.specs2.reporter.OutputDir
import java.io.{Writer, File}
import xml.{Xhtml, NodeSeq}
import org.specs2.main.Arguments
import org.specs2.clairvoyance.io.ClasspathResources

trait ClairvoyanceHtmlFileWriter extends OutputDir {

  def writeFiles(implicit args: Arguments = Arguments()) = (htmlFiles: Seq[ClairvoyanceHtml]) => {
    copyResources()
    htmlFiles foreach writeFile
  }

  protected def writeFile = (file: ClairvoyanceHtml) => {
    val reportFile = reportPath(file.url)
    fileWriter.write(reportFile)(writeXml(file.xml))
    println("Output:\n%s".format(new File(reportFile).getAbsolutePath))
  }

  protected def writeXml(xml: NodeSeq)(out: Writer) {
    out.write(Xhtml.toXhtml(xml))
  }

  protected def copyResources() {
    Seq("css", "javascript").foreach(ClasspathResources.copyResource(_, outputDir))
  }


}
