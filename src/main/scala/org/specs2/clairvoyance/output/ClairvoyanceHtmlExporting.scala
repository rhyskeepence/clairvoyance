package org.specs2.clairvoyance.output

import org.specs2.io.ConsoleOutput
import org.specs2.main.Arguments
import org.specs2.reporter._
import org.specs2.specification._
import org.specs2.internal.scalaz.Scalaz._
import scala.xml.{Xhtml, NodeSeq}
import java.io.{File, Writer}
import java.net.URL

class ClairvoyanceHtmlExporting extends Exporter with ClairvoyanceHtmlPrinter with ClairvoyanceHtmlFileWriter {
  type ExportType = Unit

  def export(implicit args: Arguments): ExecutingSpecification => ExecutedSpecification = (spec: ExecutingSpecification) => {
    val executed = spec.execute
    print(executed) |> writeFiles
    executed
  }
}

case class ClairvoyanceHtml(url: String, xml: NodeSeq)

trait HtmlReporter extends DefaultReporter with ClairvoyanceHtmlFileWriter with ConsoleOutput

trait ClairvoyanceHtmlFileWriter extends OutputDir {

  def writeFiles = (htmlFiles: Seq[ClairvoyanceHtml]) => {
    copyResources()
    htmlFiles foreach writeFile
  }

  protected def writeFile = (file: ClairvoyanceHtml) => {
    val reportFile = reportPath(file.url)
    fileWriter.write(reportFile)(writeXml(file.xml))
    printf("Output:%n%s%n", new File(reportFile).getAbsolutePath)
  }

  protected def writeXml(xml: NodeSeq)(out: Writer) {
    out.write(Xhtml.toXhtml(xml))
  }

  protected def copyResources() {
    Seq("css", "javascript").foreach(copyFromResourcesDir(_, outputDir))
  }

  private def copyFromResourcesDir(src: String, outputDir: String) {
    val jarUrl = Thread.currentThread.getContextClassLoader.getResource(getClass.getName.replace(".", "/") + ".class")
    for (url <- Option(jarUrl) if url.toString.startsWith("jar"))
      fileSystem.unjar(getPath(url).takeWhile(_ != '!').mkString, outputDir, ".*" + src + "/.*")

    val folderUrl = Thread.currentThread.getContextClassLoader.getResource(src)
    for (url <- Option(folderUrl) if !folderUrl.toString.startsWith("jar"))
      fileSystem.copyDir(url, outputDir + src)
  }

  private def getPath(url: URL) = {
    val path =
      if (sys.props("file.separator") == "\\")
        url.getPath.replace("\\", "/").replace("file:/", "")
      else
        url.getPath.replace("file:", "")

    path.replace("%20", " ")
  }
}
