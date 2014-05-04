package org.specs2.clairvoyance.export

import java.io.{File, Writer}
import org.specs2.clairvoyance.io.ClasspathResources
import org.specs2.main.Arguments
import org.specs2.reporter.OutputDir
import scala.xml.{Xhtml, NodeSeq}

trait ClairvoyanceHtmlFileWriter extends OutputDir {

  def writeFiles(implicit args: Arguments = Arguments()) = (htmlFiles: Seq[ClairvoyanceHtml]) => {
    copyResources()
    htmlFiles foreach writeFile
  }

  protected def writeFile = (file: ClairvoyanceHtml) => {
    val reportFile = reportPath(file.url)
    fileWriter.write(reportFile)(writeXml(file.xml))
    println(s"Output:\n${new File(reportFile).getAbsolutePath}")
  }

  protected def writeXml(xml: NodeSeq)(out: Writer): Unit = { out.write(Xhtml.toXhtml(xml)) }

  protected def copyResources(): Unit = {
    Seq("css", "javascript").foreach(ClasspathResources.copyResource(_, outputDir))
  }
}
