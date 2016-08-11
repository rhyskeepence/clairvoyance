package clairvoyance.export

import clairvoyance.export.NaiveMultilineMethodNameJoiner.doStripLine
import org.scalatest.{MustMatchers, WordSpec}

class NaiveMultilineMethodNameJoinerSpec extends WordSpec with MustMatchers {

  "doStripLine strips the margin of a multiline string and" must {

    "remove the explicit call to stripMargin" in {
      doStripLine("\"\"\"Bla\n    |----\n    |\n    |Foo\n    |\"\"\".stripMargin") mustBe "\"\"\"Bla\n----\n\nFoo\n\"\"\""
    }

    "work even without an explicit call to stripMargin" in {
      doStripLine("\"\"\"Bla\n    |----\n    |\n    |Foo\n    |\"\"\"") mustBe "\"\"\"Bla\n----\n\nFoo\n\"\"\""
    }
  }
}
