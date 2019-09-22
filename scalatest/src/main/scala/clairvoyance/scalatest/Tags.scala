package clairvoyance.scalatest

import clairvoyance.scalatest.tags.{skipInteractions, skipSpecification}
import org.scalatest.Tag

object SkipSpecification extends Tag(classOf[skipSpecification].getName)
object SkipInteractions  extends Tag(classOf[skipInteractions].getName)

object Tags {
  val declared = Set(SkipSpecification, SkipInteractions)
}
