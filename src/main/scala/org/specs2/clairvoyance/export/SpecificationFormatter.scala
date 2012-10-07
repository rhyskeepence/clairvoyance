package org.specs2.clairvoyance.export

import java.util.regex.Matcher

object SpecificationFormatter {
  val formattingChain = Function chain List(replaceSyntaxWithSpaces, replaceCamelCaseWithSentence, capitaliseFirstCharacterOfEachLine, formatGWTStyle, formatGWTStyleWithoutBrace)

  def format(source: String) = {
    formattingChain(source)
  }

  def replaceSyntaxWithSpaces: String => String = "[\\(\\);_\\.]".r.replaceAllIn(_, " ")

  def replaceCamelCaseWithSentence: String => String = "([a-z])([A-Z])".r.replaceAllIn(_, m => m.group(1) + " " + m.group(2).toLowerCase)

  def capitaliseFirstCharacterOfEachLine: String => String = "(?m)^([a-z])".r.replaceAllIn(_, _.group(1).toUpperCase)

  def formatGWTStyle: String => String = {
    "(?s)\"(.*?)\"[\\s+]==>[\\s+]\\{.*?\\}".r.replaceAllIn(_, m => Matcher.quoteReplacement(m.group(1)))
  }

  def formatGWTStyleWithoutBrace: String => String = { s =>
    println(s)
    "\"(.*?)\"\\s+==>\\s+.*".r.replaceAllIn(s, m => Matcher.quoteReplacement(m.group(1)))
  }
}
