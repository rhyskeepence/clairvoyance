package org.specs2.clairvoyance

import collection.mutable.ListBuffer
import org.specs2.clairvoyance.Imports._
import java.util.concurrent.atomic.AtomicInteger

trait ProducesCapturedInputsAndOutputs {

  private lazy val capturedValues = new ListBuffer[CapturedValue]

  def captureValue(capturedValue: KeyValue) {
    capturedValues += CapturedValue(CapturedValueSequence.nextId, capturedValue._1, capturedValue._2)
  }

  def clear() {
    capturedValues.clear()
  }

  def producedCapturedInputsAndOutputs = capturedValues.toList
}

case class CapturedValue(id: Int, key: String, value: AnyRef) {
  def toPair = (key, value)
}

/* This is so that captured values are in insertion order across all producers */
object CapturedValueSequence {
  private val sequence = new AtomicInteger
  def nextId = sequence.getAndIncrement
}