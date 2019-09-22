package clairvoyance.scalatest.examples

import org.scalatest.WordSpec

class GivenWhenThenExample extends WordSpec with DoomsdayContext {

  "The coordinator" must {
    "not invoke the Doomsday Device on the 20th of December 2012" in {
      "Given the date is 20/12/2012" ===> setDateTo("20/12/2012")
      "When the coordinator runs" ===> runTheCoordinator
      "Then the Doomsday device should not be unleashed" ===> theDoomsdayDevice shouldNot be(
        unleashed
      )
    }

    "invoke the Doomsday Device on the 21st of December 2012" in {
      "Given the date is 21/12/2012" ===> setDateTo("21/12/2012")
      "When the coordinator runs" ===> runTheCoordinator
      "Then the Doomsday device should be unleashed" ===> theDoomsdayDevice shouldBe unleashed
    }
  }
}
