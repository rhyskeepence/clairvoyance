package clairvoyance.scalatest.examples

import org.scalatest.Spec

class GivenWhenThenExample extends Spec with DoomsdayContext {

  object `The coordinator must` {
    def `not invoke the Doomsday Device on the 20th of December 2012`() {
      "Given the date is 20/12/2012"                      ===> setDateTo("20/12/2012")
      "When the coordinator runs"                         ===> runTheCoordinator
      "Then the Doomsday device should not be unleashed"  ===> theDoomsdayDevice shouldNot be (unleashed)
    }

    def `invoke the Doomsday Device on the 21st of December 2012`() {
      "Given the date is 21/12/2012"                  ===> setDateTo("21/12/2012")
      "When the coordinator runs"                     ===> runTheCoordinator
      "Then the Doomsday device should be unleashed"  ===> theDoomsdayDevice shouldBe unleashed
    }
  }
}
