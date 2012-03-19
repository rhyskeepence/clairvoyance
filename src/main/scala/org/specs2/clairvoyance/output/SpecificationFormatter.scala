package org.specs2.clairvoyance.output

object SpecificationFormatter {
  val formattingChain = Function chain List(replaceSyntaxWithSpaces, replaceCamelCaseWithSentence, capitaliseFirstCharacterOfEachLine)

  def format(source: String) = {
    formattingChain(source)
  }

  def replaceSyntaxWithSpaces: String => String = "[\\(\\);_\\.]".r.replaceAllIn(_, " ")

  def replaceCamelCaseWithSentence: String => String = "([a-z])([A-Z])".r.replaceAllIn(_, m => m.group(1) + " " + m.group(2).toLowerCase)

  def capitaliseFirstCharacterOfEachLine: String => String = "(?m)^([a-z])".r.replaceAllIn(_, _.group(1).toUpperCase)
}
