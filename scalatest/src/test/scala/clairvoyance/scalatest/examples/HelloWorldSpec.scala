package clairvoyance.scalatest.examples

import org.scalatest.{MustMatchers, WordSpec}

class HelloWorldSpec extends WordSpec with MustMatchers {

  "The 'Hello world' string" must {

    "contain 11 characters" in {
      "Hello world" must have size 11
    }

    "start with 'Hello'" in {
      "Hello world" must startWith("Hello")
    }

    "end with 'world'" in {
      "Hello world" must endWith("world")
    }

    "be found inside a multiline string" in {
      """
        |Begin: 12345
        |From: Foo
        |To: Bar
        |
        |Hello World
        |
        |End: 12345
      """ must include("Hello World")
    }
  }
}
