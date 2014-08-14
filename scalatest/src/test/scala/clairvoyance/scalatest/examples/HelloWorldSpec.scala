package clairvoyance.scalatest.examples

import org.scalatest.{MustMatchers, Spec}

class HelloWorldSpec extends Spec with MustMatchers {

  object `The 'Hello world' string` {
    def `must contain 11 characters`() {
      "Hello world" must have size 11
    }
    def `must start with 'Hello'`() {
      "Hello world" must startWith("Hello")
    }
    def `must end with 'world'`() {
      "Hello world" must endWith("world")
    }
    def `can be found inside a multiline string`() {
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
