package org.specs2.clairvoyance.rendering

import xml.Elem

trait CustomRendering {
  def customRendering: PartialFunction[Any, Elem]
}
