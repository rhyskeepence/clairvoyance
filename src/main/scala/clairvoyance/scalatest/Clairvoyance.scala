package clairvoyance.scalatest

import org.scalatest.{MustMatchers, Suite, SuiteMixin}

trait Clairvoyance extends SuiteMixin with MustMatchers { this: Suite => }
