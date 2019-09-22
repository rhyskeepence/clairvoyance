package clairvoyance.export

import java.util.regex.Matcher
import scala.util.Properties.lineSeparator

object SpecificationFormatter {

  def format(
      sourceLines: List[(Int, String)],
      stackTrace: Seq[StackTraceElement] = Seq.empty,
      suiteClassName: String,
      codeFormatter: CodeFormat
  ) = {
    val withHighlightedFailures = formatFailures(sourceLines, stackTrace, suiteClassName)
    val codeAsString            = withHighlightedFailures.mkString(lineSeparator)
    codeFormatter.format(codeAsString)
  }

  private def formatFailures(
      sourceLines: List[(Int, String)],
      stackTrace: Seq[StackTraceElement],
      suiteClassName: String
  ) = {
    val failureLine = failureLineNumber(sourceLines, stackTrace, suiteClassName).getOrElse(-1)
    sourceLines.map {
      case (line: Int, source: String) => if (failureLine == line) "-- " + source else source
    }
  }

  private def failureLineNumber(
      source: List[(Int, String)],
      stackTrace: Seq[StackTraceElement],
      suiteClassName: String
  ): Option[Int] = {
    val lineNumbers   = source.map(_._1)
    val classFileName = suiteClassName.split("\\.").last + ".scala"
    stackTrace
      .filter(s => s.getFileName == classFileName)
      .map(_.getLineNumber)
      .find(lineNumbers.contains(_))
  }
}

trait CodeFormat {
  def format(line: String): String
}

object DefaultCodeFormat extends CodeFormat {
  override def format(line: String) = line
}

trait HumanisedCodeFormat extends CodeFormat {
  override def format(line: String): String = {
    def replaceSyntaxWithSpaces: String => String = "[\\(\\);_\\.]".r.replaceAllIn(_, " ")

    def trimSpaces: String => String = "[ \\t]+".r.replaceAllIn(_, " ")

    def replaceCamelCaseWithSentence: String => String =
      _.replaceAll(
        String.format(
          "%s|%s|%s",
          "(?<=[A-Z])(?=[A-Z][a-z])",
          "(?<=[^A-Z])(?=[A-Z])",
          "(?<=[A-Za-z])(?=[^A-Za-z])"
        ),
        " "
      )

    def capitaliseWords: String => String =
      "(?m)(^|\\s)([a-z])".r.replaceAllIn(_, m => m.group(1) + m.group(2).toUpperCase)

    def formatGWTStyle: String => String =
      "(?s)\"(.*?)\"[\\s+]===>[\\s+]\\{.*?\\}".r
        .replaceAllIn(_, m => Matcher.quoteReplacement(m.group(1)))

    def formatGWTStyleWithoutBrace: String => String =
      "\"(.*?)\"\\s+===>\\s+.*".r.replaceAllIn(_, m => Matcher.quoteReplacement(m.group(1)))

    val formattingChain = replaceSyntaxWithSpaces andThen replaceCamelCaseWithSentence andThen capitaliseWords andThen formatGWTStyle andThen formatGWTStyleWithoutBrace andThen trimSpaces
    formattingChain(line)
  }
}
