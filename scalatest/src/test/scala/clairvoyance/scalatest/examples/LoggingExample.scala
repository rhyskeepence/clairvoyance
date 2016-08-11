package clairvoyance.scalatest.examples

import org.scalatest.{Matchers, WordSpec}

class LoggingExample extends WordSpec with DoomsdayContext with Matchers {

  "The coordinator" must {
    "invoke the Doomsday Device on the 21st of December 2012" in {
      setDateTo("21/12/2012")
      runTheCoordinator()
      theDoomsdayDevice shouldBe unleashed
    }
  }
}
