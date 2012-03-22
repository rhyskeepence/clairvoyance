package org.specs2.clairvoyance

import collection.mutable.ListBuffer
import org.specs2.clairvoyance.Imports._

trait InterestingGivens {
  val interestingGivens = new MutableInterestingGivens()

  implicit def toInterestingGiven(s: KeyValue) = new InterestingGivenBuilder(interestingGivens, s)

  class InterestingGivenBuilder(interestingGivens: MutableInterestingGivens, s: KeyValue) {
    def isInteresting {
      interestingGivens += s
    }
  }

  class MutableInterestingGivens  {
    private lazy val values = new ListBuffer[KeyValue]

    def +=[T](interestingGiven: (String, T)) {
      values += interestingGiven
    }

    def apply(key: String) = values.find(_._1 == key).map(_._2)

    def toList = values.toList
  }
}
