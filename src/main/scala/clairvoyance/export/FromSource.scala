package clairvoyance.export

import clairvoyance.io.Files.{currentWorkingDirectory, listFiles}
import java.io.File
import org.specs2.io.Location
import scala.annotation.tailrec

object FromSource {
  def getCodeFrom(location: Location): List[(Int, String)] = {
    val sourceFilePath = location.toString().split(" ")(0).replaceAll("\\.", "/") + ".scala"
    val sourceFile = listFiles(currentWorkingDirectory).find(_.getPath.endsWith(sourceFilePath))
    val content = readLines(sourceFile).getOrElse(Seq.empty)
    readToEndOfMethod(content, location.lineNumber)
  }

  @tailrec
  def readToEndOfMethod(content: Seq[String], lineNumber: Int, indentLevel: Int = 0, res: List[(Int, String)] = List()): List[(Int, String)] = {
    if (content.size < lineNumber || lineNumber < 1) {
      res.reverse

    } else if (content(lineNumber).trim().endsWith("{")) {
      readToEndOfMethod(content, lineNumber + 1, indentLevel + 1, addLine(lineNumber, content, res))

    } else if (content(lineNumber).trim().startsWith("}") && indentLevel == 0) {
      res.reverse

    } else if (content(lineNumber).trim().startsWith("}")) {
      readToEndOfMethod(content, lineNumber + 1, indentLevel - 1, addLine(lineNumber, content, res))

    } else {
      readToEndOfMethod(content, lineNumber + 1, indentLevel, addLine(lineNumber, content, res))
    }
  }

  def addLine(lineNumber: Int, content: Seq[String], lines: List[(Int, String)]): List[(Int, String)] =
    (lineNumber + 1, content(lineNumber).trim()) :: lines

  def readLines(path: Option[File]) =
    path.map(scala.io.Source.fromFile(_).getLines().toIndexedSeq)
}
