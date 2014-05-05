package clairvoyance.specs2.export

import java.util.regex.Matcher
import org.specs2.execute.{Failure, Result}

object SpecificationFormatter {
  val formattingChain = replaceSyntaxWithSpaces andThen replaceCamelCaseWithSentence andThen capitaliseFirstCharacterOfEachLine andThen formatGWTStyle andThen formatGWTStyleWithoutBrace

  def format(result: Result, sourceLines: List[(Int, String)]) = {
    val withHighlightedFailures = formatFailures(result, sourceLines)
    val codeAsString = withHighlightedFailures.mkString("\n")
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

  private def formatFailures(result: Result, sourceLines: List[(Int, String)]) = {
    val failureLine = failureLineNumber(result, sourceLines).getOrElse(-1)
    sourceLines.map {
      case (line: Int, source: String) =>
        if (failureLine == line)
          "-- " + source
        else
          source
    }
  }

  private def failureLineNumber(result: Result, source: List[(Int, String)]) = {
    val lineNumbers = source.map(_._1)
    val stackTrace: Seq[StackTraceElement] = result match {
      case Failure(_, _, st, _) => st
      case _ => Seq.empty
    }
    stackTrace
      .filter(!_.getClassName.startsWith("org.specs2"))
      .map(_.getLineNumber)
      .find(lineNumbers.contains(_))
  }
}
