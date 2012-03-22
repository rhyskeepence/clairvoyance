package org.specs2.clairvoyance.export

import org.specs2.io.Paths._
import org.specs2.io.Location
import org.specs2.main.SystemProperties

object FromSource {
  lazy val srcDir: String = SystemProperties.getOrElse("srcTestDir", "src/test/scala").dirPath

  def getCodeFrom(location: Location) = {
    val fullClassName = location.toString().split(" ")(0).replaceAll("\\.", "/") + ".scala"
    val content = readLines(srcDir + fullClassName)
    readToEndingBrace(content, location.lineNumber).mkString("\n")
  }

  def readToEndingBrace(content: Seq[String], lineNumber: Int, res: List[String] = List()): List[String] = {
    if (content(lineNumber).trim().startsWith("}")) {
      res.reverse
    } else {
      readToEndingBrace(content, lineNumber + 1, content(lineNumber).trim() :: res)
    }
  }

  def readLines(path: String) = scala.io.Source.fromFile(path).getLines.toIndexedSeq

}
