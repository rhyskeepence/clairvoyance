package org.specs2.clairvoyance

import collection.mutable.ListBuffer
import org.specs2.clairvoyance.Imports._
import java.util.concurrent.atomic.AtomicInteger

trait ProducesCapturedInputsAndOutputs {

  private lazy val capturedValues = new ListBuffer[(Int, KeyValue)]

  def captureValue(capturedValue: KeyValue) {
    capturedValues += ((CapturedValueSequence.nextId, capturedValue))
  }

  def clear() {
    capturedValues.clear()
  }

  def producedCapturedInputsAndOutputs = capturedValues.toList
}

/* This is so that captured values are in insertion order across all producers */
object CapturedValueSequence {
  private val sequence = new AtomicInteger
  def nextId = sequence.getAndIncrement
}