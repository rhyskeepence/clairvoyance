package clairvoyance.scalatest.examples

import org.scalatest.{Matchers, Spec}

class LoggingExample extends Spec with DoomsdayContext with Matchers {

  object `The coordinator must` {
    def `invoke the Doomsday Device on the 21st of December 2012`() {
      setDateTo("21/12/2012")
      runTheCoordinator()
      theDoomsdayDevice shouldBe unleashed
    }
  }
}
