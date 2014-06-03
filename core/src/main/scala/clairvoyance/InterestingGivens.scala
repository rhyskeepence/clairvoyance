package clairvoyance

import clairvoyance.Imports.KeyValue
import scala.collection.mutable.ListBuffer

trait InterestingGivens {
  val interestingGivens = new MutableInterestingGivens()

  implicit def toInterestingGiven(s: KeyValue) = new InterestingGivenBuilder(interestingGivens, s)

  class InterestingGivenBuilder(interestingGivens: MutableInterestingGivens, s: KeyValue) {
    def isInteresting(): Unit = {
      interestingGivens += s
    }
  }

  class MutableInterestingGivens  {
    private lazy val values = new ListBuffer[KeyValue]

    def +=[T <: AnyRef](interestingGiven: (String, T)): Unit = {
      values += interestingGiven
    }

    def apply(key: String) = values.find(_._1 == key).map(_._2)

    def toList = values.toList
  }
}
