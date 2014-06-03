package clairvoyance.export

import java.util.regex.Matcher
import scala.util.Properties.lineSeparator

object SpecificationFormatter {
  val formattingChain = replaceSyntaxWithSpaces andThen replaceCamelCaseWithSentence andThen capitaliseFirstCharacterOfEachLine andThen formatGWTStyle andThen formatGWTStyleWithoutBrace

  def format(sourceLines: List[(Int, String)], stackTrace: Seq[StackTraceElement] = Seq.empty) = {
    val withHighlightedFailures = formatFailures(sourceLines, stackTrace)
    val codeAsString = withHighlightedFailures.mkString(lineSeparator)
    formattingChain(codeAsString)
  }

  private def replaceSyntaxWithSpaces: String => String =
    "[\\(\\);_\\.]".r.replaceAllIn(_, " ")

  private def replaceCamelCaseWithSentence: String => String =
    "([a-z])([A-Z])".r.replaceAllIn(_, m => m.group(1) + " " + m.group(2).toLowerCase)

  private def capitaliseFirstCharacterOfEachLine: String => String =
    "(?m)^([a-z])".r.replaceAllIn(_, _.group(1).toUpperCase)

  private def formatGWTStyle: String => String =
    "(?s)\"(.*?)\"[\\s+]===>[\\s+]\\{.*?\\}".r.replaceAllIn(_, m => Matcher.quoteReplacement(m.group(1)))

  private def formatGWTStyleWithoutBrace: String => String =
    "\"(.*?)\"\\s+===>\\s+.*".r.replaceAllIn(_, m => Matcher.quoteReplacement(m.group(1)))

  private def formatFailures(sourceLines: List[(Int, String)], stackTrace: Seq[StackTraceElement]) = {
    val failureLine = failureLineNumber(sourceLines, stackTrace).getOrElse(-1)
    sourceLines.map {
      case (line: Int, source: String) => if (failureLine == line) "-- " + source else source
    }
  }

  private def failureLineNumber(source: List[(Int, String)], stackTrace: Seq[StackTraceElement]): Option[Int] = {
    val lineNumbers = source.map(_._1)
    stackTrace
      .filter(!_.getClassName.startsWith("org.specs2"))
      .map(_.getLineNumber)
      .find(lineNumbers.contains(_))
  }
}
