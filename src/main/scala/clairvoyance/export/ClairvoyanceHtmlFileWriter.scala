package clairvoyance.export

import clairvoyance.export.ClairvoyanceHtmlFileWriter.{copyResourcesOnlyOnce, outputDir}
import clairvoyance.io.ClasspathResources
import java.io.Writer
import java.util.concurrent.atomic.AtomicBoolean
import scala.util.Properties.{propOrElse, userDir}
import scala.xml.{Xhtml, NodeSeq}
import scalax.file.Path

trait ClairvoyanceHtmlFileWriter {

  def writeFiles = (htmlFiles: Seq[ClairvoyanceHtml]) => {
    copyResources()
    htmlFiles foreach writeFile
  }

  protected def writeFile = (file: ClairvoyanceHtml) => {
    val reportFile = Path.fromString(outputDir + file.url)
    reportFile.write(Xhtml.toXhtml(file.xml))
    println(s"Output:\n${reportFile.toAbsolute.path}")
  }

  protected def writeXml(xml: NodeSeq)(out: Writer): Unit = out.write(Xhtml.toXhtml(xml))
  protected def copyResources(): Unit = copyResourcesOnlyOnce()
}

private object ClairvoyanceHtmlFileWriter {
  private val outputDir = propOrElse("specs2.outDir", s"$userDir/target/specs2-reports/")
  private[this] val resourcesNotCopied = new AtomicBoolean(true)

  private def copyResourcesOnlyOnce(): Unit = {
    if (resourcesNotCopied.getAndSet(false)) {
      Seq("css", "javascript").foreach(ClasspathResources.copyResource(_, outputDir))
    }
  }
}
