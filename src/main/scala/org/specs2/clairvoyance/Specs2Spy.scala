package org.specs2.clairvoyance

import org.specs2.io.ConsoleOutput
import org.specs2.reflect.Classes
import org.specs2.text.Markdown
import org.specs2.specification.{Fragment, SpecificationStructure}

object Specs2Spy {
  def fragmentsOf(specificationStructure: SpecificationStructure): Seq[Fragment] = specificationStructure.content.fragments

  object Classes extends Classes with ConsoleOutput
  object Markdown extends Markdown
}
