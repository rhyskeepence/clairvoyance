package org.specs2.clairvoyance.export

import org.specs2.io.Location
import java.io.File
import org.specs2.clairvoyance.io.Files._

object FromSource {
  def getCodeFrom(location: Location) = {
    val sourceFilePath = location.toString().split(" ")(0).replaceAll("\\.", "/") + ".scala"
    val sourceFile = listFiles(currentWorkingDirectory).find(_.getPath.endsWith(sourceFilePath))
    val content = readLines(sourceFile).getOrElse(Seq.empty)
    readToEndingBrace(content, location.lineNumber).mkString("\n")
  }

  def readToEndingBrace(content: Seq[String], lineNumber: Int, indentLevel: Int = 0, res: List[String] = List()): List[String] = {
    if (content.size < lineNumber || lineNumber < 1) {
      res.reverse

    } else if (content(lineNumber).trim().endsWith("{")) {
      readToEndingBrace(content, lineNumber + 1, indentLevel + 1, content(lineNumber).trim() :: res)

    } else if (content(lineNumber).trim().startsWith("}") && indentLevel == 0) {
      res.reverse

    } else if (content(lineNumber).trim().startsWith("}")) {
      readToEndingBrace(content, lineNumber + 1, indentLevel - 1, content(lineNumber).trim() :: res)

    } else {
      readToEndingBrace(content, lineNumber + 1, indentLevel, content(lineNumber).trim() :: res)
    }
  }

  def readLines(path: Option[File]) =
    path.map(scala.io.Source.fromFile(_).getLines.toIndexedSeq)

}
