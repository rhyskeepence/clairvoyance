package clairvoyance.export

import clairvoyance.export.NaiveMultilineMethodNameJoiner.doStripLine
import org.scalatest.{MustMatchers, Spec}

class NaiveMultilineMethodNameJoinerSpec extends Spec with MustMatchers {

  object `doStripLine strips the margin of a multiline string` {

    def `and removes the explicit call to stripMargin`() {
      doStripLine("\"\"\"Bla\n    |----\n    |\n    |Foo\n    |\"\"\".stripMargin") mustBe "\"\"\"Bla\n----\n\nFoo\n\"\"\""
    }

    def `even without an explicit call to stripMargin`() {
      doStripLine("\"\"\"Bla\n    |----\n    |\n    |Foo\n    |\"\"\"") mustBe "\"\"\"Bla\n----\n\nFoo\n\"\"\""
    }
  }
}
