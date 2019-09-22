package clairvoyance.export

import org.specs2.mutable.Specification

class FromSourceTest extends Specification {
  sequential

  "Source Reader" should {
    "read simple test method" in {
      val content =
        "class {" ::
          "  startOfTest {" ::
          "    some test stuff" ::
          "  }" ::
          "}" :: Nil

      val lines = FromSource.readToEndOfMethod(content, lineNumber = 1)
      lines.size must beEqualTo(3)
    }

    "read test method with nested blocks" in {
      val content =
        "class {" ::
          "  startOfTest {" ::
          "    if () {" ::
          "    } else {" ::
          "    }" ::
          "    for {" ::
          "      y <- x" ::
          "    } yield {" ::
          "    }" ::
          "    more test stuff" ::
          "  }" ::
          "}" :: Nil

      val lines = FromSource.readToEndOfMethod(content, lineNumber = 1)
      lines.size must beEqualTo(10)
    }

    "read single line test" in {
      val content =
        "class {" ::
          "  startOfTest { blah }" ::
          "}" :: Nil

      val lines = FromSource.readToEndOfMethod(content, lineNumber = 1)
      lines.size must beEqualTo(1)
    }

    "read test without a block" in {
      val content =
        "class {" ::
          "  startOfTest = finish" ::
          "}" :: Nil

      val lines = FromSource.readToEndOfMethod(content, lineNumber = 1)
      lines.size must beEqualTo(1)
    }
  }
}
