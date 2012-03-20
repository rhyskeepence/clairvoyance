package org.specs2.clairvoyance

import org.junit.runner.RunWith
import collection.mutable.ListBuffer
import org.specs2.specification.Scope
import org.specs2.mutable.{After, Specification}
import output.{TestState, TestStates}

/**
 * The base class - the only thing to note is that we use mutable specifications rather than immutable.
 * This is not ideal but mutable specs may look a bit scary and magic to the intended audience.
 */

@RunWith(classOf[ClairvoyanceRunner])
abstract class ClairvoyantSpec extends Specification {
  sequential
  args.report(exporter = "org.specs2.clairvoyance.ClairvoyanceHtmlExporting")

  trait ClairvoyantContext extends Scope with After with InterestingGivens {
    def capturedInputsAndOutputs = Seq[ProducesCapturedInputsAndOutputs]()

    def tearDown {}
    
    def after {
      tearDown
      val captured = capturedInputsAndOutputs.map(_.producedCapturedInputsAndOutputs).flatten
      capturedInputsAndOutputs.foreach(_.clear())

      TestStates += TestState(interestingGivens.toList, captured)
    }
  }

  trait InterestingGivens {
    val interestingGivens = new MutableInterestingGivens()
    implicit def toInterestingGiven(s: (String, Any)) = new InterestingGivenBuilder(interestingGivens, s)
  }
}

class InterestingGivenBuilder(interestingGivens: MutableInterestingGivens, s: (String, Any)) {
  def isInteresting {
    interestingGivens += s
  }
}

class MutableInterestingGivens  {
  private lazy val values = new ListBuffer[(String, Any)]

  def +=[T](interestingGiven: (String, T)) {
    values += interestingGiven
  }

  def apply(key: String) = values.find(_._1 == key).map(_._2)

  def toList = values.toList
}