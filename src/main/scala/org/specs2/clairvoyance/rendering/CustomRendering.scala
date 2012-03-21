package org.specs2.clairvoyance.rendering

trait CustomRendering {
  def customRendering: PartialFunction[Any, String]
}
