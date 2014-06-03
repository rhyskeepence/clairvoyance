package clairvoyance.scalatest.examples

import clairvoyance.scalatest.Clairvoyance
import org.scalatest.Spec

class HelloWorldSpec extends Spec with Clairvoyance {
  object `The 'Hello world' string must` {
    def `contain 11 characters` {
      "Hello world" must have size 11
    }
    def `start with 'Hello'` {
      "Hello world" must startWith("Hello")
    }
    def `end with 'world'` {
      "Hello world" must endWith("world")
    }
  }
}
