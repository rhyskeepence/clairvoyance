package org.specs2.clairvoyance

import org.junit.runner.RunWith
import collection.mutable.ListBuffer
import org.specs2.specification.Scope
import org.specs2.mutable.{After, Specification}
import collection.mutable

/**
 * The base class - the only thing to note is that we use mutable specifications rather than immutable.
 * This is not ideal but mutable specs may look a bit scary and magic to the intended audience.
 */

@RunWith(classOf[ClairvoyanceRunner])
class ClairvoyantSpec extends Specification {
  sequential

  /* This is mega dodgy but works. Figure out a better way of reporting state to the Html reporter - perhaps by extending the `in` keyword */
  val testStates = new mutable.Queue[TestState] 

  trait ClairvoyantContext extends Scope with After with InterestingGivens {
    def capturedInputsAndOutputs = Seq[ProducesCapturedInputsAndOutputs]()

    def tearDown {}
    
    def after {
      tearDown
      val captured = capturedInputsAndOutputs.map(_.producedCapturedInputsAndOutputs).flatten
      capturedInputsAndOutputs.foreach(_.clear())

      testStates += TestState(interestingGivens.toList, captured)
    }
  }

  trait InterestingGivens {
    val interestingGivens = new MutableInterestingGivens()
    implicit def toInterestingGiven(s: (String, Any)) = new InterestingGivenBuilder(interestingGivens, s)
  }
}

case class TestState(interestingGivens: Seq[(String, Any)], capturedInputsAndOutputs: Seq[(String, Any)])

class InterestingGivenBuilder(interestingGivens: MutableInterestingGivens, s: (String, Any)) {
  def isInteresting {
    interestingGivens += s
  }
}

class MutableInterestingGivens  {
  private lazy val values = new ListBuffer[(String, Any)]

  def this(interestingGivens: MutableInterestingGivens) {
    this()
    values ++= interestingGivens.toList
  }

  def +=[T](interestingGiven: (String, T)) {
    values += interestingGiven
  }


  def apply(key: String) = values.find(_._1 == key).map(_._2)

  def toList = values.toList
}