package clairvoyance.scalatest

import clairvoyance.scalatest.tags.{skipInteractions, skipSpecification}
import org.scalatest.Tag

object SkipSpecification extends Tag(classOf[skipSpecification].toString)
object SkipInteractions  extends Tag(classOf[skipInteractions].toString)
