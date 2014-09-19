package clairvoyance.scalatest.export

import org.scalatest.events.Event
import org.scalatest.{Reporter, ResourcefulReporter}

class ForwardingReporter(reporters: Reporter*) extends ResourcefulReporter {
  def apply(e: Event): Unit = reporters.foreach(_(e))

  override def dispose() = reporters.foreach {
    case r: ResourcefulReporter => r.dispose()
  }
}
